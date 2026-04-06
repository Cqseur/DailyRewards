package cqseur.dailyrewards

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.text.ClickEvent
import java.net.URI

import cqseur.dailyrewards.RewardFetcher
import cqseur.dailyrewards.utils.MessageUtils
import cqseur.dailyrewards.utils.manager.DailyClaimManager

object RewardClaimer {
    private val logger = LoggerFactory.getLogger("[DailyRewards-Client]")
    private val client = OkHttpClient()
    
    fun claim(option: Int, id: String) {
        val token = RewardFetcher.securityToken
        val cookieHeader = RewardFetcher.cookies.joinToString("; ")
        if (token.isNullOrEmpty() || cookieHeader.isEmpty()) {
            MinecraftClient.getInstance().execute {
                MessageUtils.sendError("Cannot claim, missing token/cookies")
                val githubMessage = MessageUtils.PREFIX()
                    .append(Text.literal("If the error persist, create an issue on the "))
                    .append(Text.literal("[GitHub]")
                        .formatted(Formatting.DARK_AQUA, Formatting.BOLD, Formatting.UNDERLINE)
                        .styled { it.withClickEvent(ClickEvent.OpenUrl(URI("https://github.com/Cqseur/DailyRewards/issues"))) })
                MinecraftClient.getInstance().player?.sendMessage(githubMessage, false)
            }
            logger.warn("Missing token or cookies, cannot claim reward")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlWithParams = "https://rewards.hypixel.net/claim-reward/claim?id=${id}&option=${option}&activeAd=${RewardFetcher.activeAd}&_csrf=${token}"
                val request = Request.Builder()
                    .url(urlWithParams)
                    .post("".toRequestBody())
                    .header("X-CSRF-Token", token)
                    .header("Referer", "https://rewards.hypixel.net/")
                    .header("Origin", "https://rewards.hypixel.net")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Cookie", cookieHeader)
                    .build()
                client.newCall(request).execute().use { resp ->
                    val respBody = resp.body?.string()
                    logger.warn("Claim response body: $respBody")
                    if (resp.isSuccessful) {
                        logger.info("Claim successful for option $option id $id code ${resp.code}")
                        DailyClaimManager.recordClaim()
                    } else {
                        logger.warn("Claim failed code ${resp.code}")
                        MinecraftClient.getInstance().execute {
                            MessageUtils.sendError("Claim failed (${resp.code})")
                            val githubMessage = MessageUtils.PREFIX()
                                .append(Text.literal("If the error persist, create an issue on the "))
                                .append(Text.literal("[GitHub]")
                                    .formatted(Formatting.DARK_AQUA, Formatting.BOLD, Formatting.UNDERLINE)
                                    .styled { it.withClickEvent(ClickEvent.OpenUrl(URI("https://github.com/Cqseur/DailyRewards/issues"))) })
                            MinecraftClient.getInstance().player?.sendMessage(githubMessage, false)
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("Error during claim", e)
            }
        }
    }
}
