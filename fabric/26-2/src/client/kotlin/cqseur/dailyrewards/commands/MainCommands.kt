package cqseur.dailyrewards.commands

import cqseur.dailyrewards.ui.DailyRewardsConfigScreen
import cqseur.dailyrewards.ui.RewardScreen
import cqseur.dailyrewards.utils.MessageUtils
import cqseur.dailyrewards.utils.manager.DailyClaimManager
import cqseur.dailyrewards.RewardOffer
import cqseur.dailyrewards.RewardCard

import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
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
            ClientCommands.literal("dailyrewards")
                .executes {
                    pendingOpenConfig = true
                    1
                }
                .then(ClientCommands.literal("config")
                    .executes {
                        pendingOpenConfig = true
                        1
                    }
                )
                .then(ClientCommands.literal("status")
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
        val player = Minecraft.getInstance().player

        if (canClaim) {
            MessageUtils.sendMessage("§6⏰ Your reward is waiting to be claimed!")
        } else {
            MessageUtils.sendSuccess("✅ You have already claimed your daily reward today")
        }

        sendRainbowSeparator()
        player?.sendSystemMessage(Component.literal("§d📊 Current streak: §a$streakInfo§r"))
        player?.sendSystemMessage(Component.literal("§e⏰ Time until reset: §6$nextResetHypixel§r"))
        player?.sendSystemMessage(Component.literal("§b📅 Next reset Full Date: §3$nextResetDate§r"))
        sendRainbowSeparator()
    }
    
    private fun sendRainbowSeparator() {
        val player = Minecraft.getInstance().player
    
        val start = 0xF2C511 /* #F2C511 */
        val color2 = 0xF39C19 /* #F39C19 */
        val color3 = 0xFF0000 /* #FF0000 */
        val color4 = 0xb300ff /* #b300ff */
        val PREFIX = MessageUtils.gradientText("DailyRewards", start, color2, color3, color4)
        
        val separator = Component.literal("§6▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬ ")
            .append(PREFIX)
            .append(Component.literal(" §6▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§r"))
        player?.sendSystemMessage(separator)
    }
    
    fun shouldOpenConfig(): Boolean {
        if (pendingOpenConfig) {
            pendingOpenConfig = false
            return true
        }
        return false
    }
    
    fun openConfigScreen() {
        Minecraft.getInstance().setScreenAndShow(DailyRewardsConfigScreen.create(null))
    }
}
