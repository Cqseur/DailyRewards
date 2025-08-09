package cqseur.dailyrewards

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import kotlinx.serialization.json.*
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text


data class RewardCard(
    val name: String,
    val amount: String,
    val description: String,
    val rarity: String,
    val iconUrl: String
)

data class RewardOffer(
    val id: String, 
    val cards: List<RewardCard>
)

object RewardFetcher {
    private fun iconNameFor(type: String): String = when(type) {
        "coins", "dust", "souls", "experience", "mystery_box", "adsense_token" -> type
        else -> "chest_open"
    }
    private val logger = LoggerFactory.getLogger("[DailyRewards-Client]")
    var securityToken: String? = null
    var cookies: List<String> = emptyList()
    var activeAd: Int = 0
    var currentStreak: Int = 0
    var highestStreak: Int = 0
    private val client = OkHttpClient()

    private fun gameNameFor(type: String): String = when(type) {
        "WALLS3" -> "Mega Walls"
        "SURVIVAL_GAMES" -> "Blitz SG"
        "TNTGAMES" -> "TNT Games"
        "ARCADE" -> "Arcade"
        "UHC" -> "UHC"
        "MCGO" -> "Cops and Crims"
        "BATTLEGROUND" -> "Warlords"
        "SUPER_SMASH" -> "Smash Heroes"
        "SKYWARS" -> "SkyWars"
        "BEDWARS" -> "Bed Wars"
        "BUILD_BATTLE" -> "Build Battle"
        "MURDER_MYSTERY" -> "Murder Mystery"
        "DUELS" -> "Duels"
        "LEGACY" -> "Classic"
        else -> type.replaceFirstChar { it.titlecase() }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { ch -> ch.titlecase() } }

    fun fetch(id: String, onOffer: (RewardOffer) -> Unit = { offer ->
        val mc = MinecraftClient.getInstance()
        mc.execute {
            val player = mc.player
            offer.cards.forEachIndexed { idx, card ->
            }
        }
    }) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://rewards.hypixel.net/claim-reward/$id"
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { resp ->
                    logger.info("Fetched reward page: status ${resp.code}")
                    cookies = resp.headers("set-cookie").map { it.substringBefore(";") }
                    logger.info("Cookies found: ${cookies.size}")
                    val body = resp.body?.string()
                    val tokenRegex = Regex("window\\.securityToken\\s*=\\s*\"([^\"]+)\"")
                    val csrfRegex = Regex("['\"]_csrf['\"]\\s*[:=]\\s*['\"]([^'\"]+)")
                    securityToken = body?.let {
                        tokenRegex.find(it)?.groupValues?.getOrNull(1)
                            ?: csrfRegex.find(it)?.groupValues?.getOrNull(1)
                    }
                    if (securityToken == null && body != null) {
                        try {
                            val doc = Jsoup.parse(body)
                            securityToken = doc.select("input[name=_csrf]").firstOrNull()?.attr("value")
                                ?: doc.select("meta[name=csrf-token]").firstOrNull()?.attr("content")
                            logger.info("Fallback Jsoup token extraction result: ${securityToken != null}")
                        } catch (e: Exception) {
                            logger.warn("Jsoup fallback token parse failed", e)
                        }
                    }
                    logger.info("Token found: ${securityToken != null}")
                    if (body == null) {
                        logger.warn("Empty body for reward page $id")
                        return@use
                    }
                    val appDataRegex = Regex("window\\.appData\\s*=\\s*'([\\s\\S]*?)';")
                    val match = appDataRegex.find(body)
                    if (match == null) {
                        logger.warn("appData not found in reward page $id")
                        return@use
                    }
                    val jsonStr = match.groupValues[1].replace("\\'", "'")
                    val jsonElement = try {
                        Json { ignoreUnknownKeys = true; isLenient = true }
                            .parseToJsonElement(jsonStr)
                    } catch (e: Exception) {
                        logger.warn("Failed to parse appData json", e)
                        return@use
                    }

                    val json = jsonElement.jsonObject

                    val i18nRegex = Regex("window\\.i18n\\s*=\\s*(\\{[\\s\\S]*?});")
                    val i18nMatch = i18nRegex.find(body)
                    val i18nMap: JsonObject = if (i18nMatch != null) {
                        var i18nStr = i18nMatch.groupValues[1]
                            i18nStr = i18nStr.replace("\\'", "'")
                            i18nStr = i18nStr.replace("'", "\\u0027")
                        try {
                            Json { ignoreUnknownKeys = true; isLenient = true }
                                .parseToJsonElement(i18nStr).jsonObject
                        } catch (e: Exception) {
                            logger.warn("Failed to parse i18n json", e)
                            JsonObject(emptyMap())
                        }
                    } else {
                        logger.warn("i18n block not found, falling back to raw names")
                        JsonObject(emptyMap())
                    }

                    activeAd = json["activeAd"]?.jsonPrimitive?.int ?: 0

                    json["dailyStreak"]?.jsonObject?.let { ds ->
                        currentStreak = ds["value"]?.jsonPrimitive?.int ?: 0
                        highestStreak = ds["highScore"]?.jsonPrimitive?.int ?: 0
                    }

                    val rewardsArray = json["rewards"]?.jsonArray ?: run {
                        logger.warn("rewards array missing in appData for $id")
                        return@use
                    }
                    val cards = rewardsArray.take(3).mapIndexed { index: Int, element: JsonElement ->
                        val obj = element.jsonObject
                        val rewardType = obj["reward"]?.jsonPrimitive?.content ?: "unknown"
                        val rarity = obj["rarity"]?.jsonPrimitive?.content ?: "common"
                        val amountStr = obj["amount"]?.jsonPrimitive?.content ?: "1"

                        var displayName = i18nMap["type.$rewardType"]?.jsonPrimitive?.content
                            ?: rewardType.replace('_', ' ').capitalizeWords()

                        if (obj["gameType"] != null) {
                            val gameType = obj["gameType"]!!.jsonPrimitive.content
                            val gameName = gameNameFor(gameType)
                            displayName = displayName.replace("{\$game}", gameName).replace("{game}", gameName)
                            if (!displayName.contains(gameName, ignoreCase = true) && rewardType in listOf("coins", "tokens")) {
                                displayName = "$gameName ${displayName.capitalizeWords()}"
                            }
                        }

                        val descKeys = listOf(
                            "string.reward_description.$rewardType",
                            "string.reward.$rewardType"
                        )
                        var descriptionStr = descKeys.firstNotNullOfOrNull { key ->
                            i18nMap[key]?.jsonPrimitive?.content
                        } ?: if (obj["gameType"] != null) {
                            "In-game $displayName"
                        } else displayName

                        if (obj["gameType"] != null) {
                            val gameType = obj["gameType"]!!.jsonPrimitive.content
                            val gameName = gameNameFor(gameType)
                            descriptionStr = descriptionStr.replace("{\$game}", gameName).replace("{game}", gameName)
                        }

                        val genericDesc = mapOf(
                            "Dust" to "Used to craft cosmetic items",
                            "coins" to "In-game Currency",
                            "souls" to "Soul Fragments used in SkyWars",
                            "experience" to "Grants Hypixel XP",
                            "mystery_box" to "Contains Housing items or cosmetics",
                            "adsense_token" to "Daily Reward Token"
                        )
                        if (rewardType == "dust") {
                            displayName = "Mystery Dust"
                        }

                        if (descriptionStr.equals(displayName, ignoreCase = true)) {
                            descriptionStr = genericDesc[rewardType] ?: descriptionStr
                        }

                        RewardCard(
                            name = displayName,
                            amount = amountStr,
                            description = descriptionStr,
                            rarity = rarity.lowercase(),
                            iconUrl = iconNameFor(rewardType)
                        )
                    }
                    val offer = RewardOffer(id, cards)
                    MinecraftClient.getInstance().execute {
                        onOffer(offer)
                        net.minecraft.client.MinecraftClient.getInstance().setScreen(cqseur.dailyrewards.ui.RewardScreen(offer))
                    }
                }
            } catch (e: Exception) {
                logger.error("Failed to fetch reward page", e)
            }
        }
    }
}
