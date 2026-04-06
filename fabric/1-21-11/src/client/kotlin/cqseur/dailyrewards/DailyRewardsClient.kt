package cqseur.dailyrewards

import cqseur.dailyrewards.RewardFetcher
import cqseur.dailyrewards.DailyReminder
import cqseur.dailyrewards.ModSoundEvents
import cqseur.dailyrewards.ui.RewardScreen
import cqseur.dailyrewards.utils.MessageUtils
import cqseur.dailyrewards.config.ConfigManager
import cqseur.dailyrewards.commands.MainCommands
import cqseur.dailyrewards.commands.CommandRegistry
import cqseur.dailyrewards.ui.DailyRewardsConfigScreen
import cqseur.dailyrewards.utils.manager.DailyClaimManager

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import net.minecraft.network.message.MessageType
import net.minecraft.network.message.SignedMessage
import net.minecraft.text.Text
import java.time.Instant
import java.util.regex.Pattern
import org.slf4j.LoggerFactory

class DailyRewardsClient : ClientModInitializer {
    private var pendingOffer: RewardOffer? = null
    private val logger = LoggerFactory.getLogger("[DailyRewards-Client]")

    private val linkPattern: Pattern = Pattern.compile("https://rewards\\.hypixel\\.net/claim-reward/([a-zA-Z0-9_-]+)")
 
    override fun onInitializeClient() {
        setInstance(this)
        
        ConfigManager.init()
        ModSoundEvents.init()
        DailyReminder.init()
        CommandRegistry.registerAll()

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (MainCommands.shouldOpenConfig()) {
                MainCommands.openConfigScreen()
            }
            
            pendingOffer?.let { offer ->
                if (client.currentScreen == null) {
                    client.setScreen(RewardScreen(offer))
                    pendingOffer = null
                }
            }
        }

        ClientReceiveMessageEvents.GAME.register { msg: Text, overlay: Boolean ->
            val matcher = linkPattern.matcher(msg.string)
            if (matcher.find()) {
                val id = matcher.group(1)
                RewardFetcher.fetch(id)
            }
        }
    }
    
    companion object {
        private var instance: DailyRewardsClient? = null
        
        /**
         * debug
         **/
        fun setPendingOffer(offer: RewardOffer) {
            instance?.pendingOffer = offer
        }
        
        internal fun setInstance(client: DailyRewardsClient) {
            instance = client
        }
    }
}