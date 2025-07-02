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

import cqseur.dailyrewards.RewardFetcher
import cqseur.dailyrewards.config.DailyRewardsConfig

object RewardClaimer {
    private val logger = LoggerFactory.getLogger("[DailyRewards-Client]")
    private val client = OkHttpClient()
    
    fun claim(option: Int, id: String) {
        val token = RewardFetcher.securityToken
        val cookieHeader = RewardFetcher.cookies.joinToString("; ")
        if (token.isNullOrEmpty() || cookieHeader.isEmpty()) {
            MinecraftClient.getInstance().execute {
                MinecraftClient.getInstance().player?.sendMessage(
                    Text.literal(DailyRewardsConfig.PREFIX + "§cCannot claim, missing token/cookies"), false
                )
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
                    } else {
                        logger.warn("Claim failed code ${resp.code}")
                        MinecraftClient.getInstance().execute {
                            MinecraftClient.getInstance().player?.sendMessage(
                                Text.literal(DailyRewardsConfig.PREFIX + "§cClaim failed (${resp.code})"), false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("Error during claim", e)
            }
        }
    }
}
