package cqseur.dailyrewards.commands

import cqseur.dailyrewards.ui.DailyRewardsConfigScreen
import cqseur.dailyrewards.ui.RewardScreen
import cqseur.dailyrewards.utils.MessageUtils
import cqseur.dailyrewards.utils.manager.DailyClaimManager
import cqseur.dailyrewards.RewardOffer
import cqseur.dailyrewards.RewardCard

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.slf4j.LoggerFactory

object MainCommands {
    private val logger = LoggerFactory.getLogger("[DailyRewards-MainCommands]")
    private var pendingOpenConfig = false
    
    fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            registerCoreCommand(dispatcher)
        }
    }
    
    private fun registerCoreCommand(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommandManager.literal("dailyrewards")
                .executes {
                    pendingOpenConfig = true
                    1
                }
                .then(ClientCommandManager.literal("config")
                    .executes {
                        pendingOpenConfig = true
                        1
                    }
                )
                .then(ClientCommandManager.literal("status")
                    .executes {
                        showClaimStatus()
                        1
                    }
                )
        )
    }
    
    private fun showClaimStatus() {
        val canClaim = DailyClaimManager.canClaimToday()
        val streakInfo = DailyClaimManager.getStreakInfo()
        val nextResetDate = DailyClaimManager.getNextResetFullTimeFormatted()
        val nextResetHypixel = DailyClaimManager.getTimeUntilNextReset()
        val player = MinecraftClient.getInstance().player

        if (canClaim) {
            MessageUtils.sendSuccess("✅ You can claim your daily reward today!")
        } else {
            MessageUtils.sendError("❌ You have already claimed your daily reward today")
        }

        sendRainbowSeparator()
        player?.sendMessage(Text.literal("§d📊 Current streak: §a$streakInfo§r"), false)
        player?.sendMessage(Text.literal("§e⏰ Time until reset: §6$nextResetHypixel§r"), false)
        player?.sendMessage(Text.literal("§b📅 Next reset Full Date: §3$nextResetDate§r"), false)
        sendRainbowSeparator()
    }
    
    private fun sendRainbowSeparator() {
        val player = MinecraftClient.getInstance().player
    
        val start = 0xF2C511 /* #F2C511 */
        val color2 = 0xF39C19 /* #F39C19 */
        val color3 = 0xFF0000 /* #FF0000 */
        val color4 = 0xb300ff /* #b300ff */
        val PREFIX = MessageUtils.gradientText("DailyRewards", start, color2, color3, color4)
        
        val separator = Text.literal("§6▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ ")
            .append(PREFIX)
            .append(Text.literal(" §6▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§r"))
        player?.sendMessage(separator, false)
    }
    
    fun shouldOpenConfig(): Boolean {
        if (pendingOpenConfig) {
            pendingOpenConfig = false
            return true
        }
        return false
    }
    
    fun openConfigScreen() {
        MinecraftClient.getInstance().setScreen(DailyRewardsConfigScreen.create(null))
    }
}
