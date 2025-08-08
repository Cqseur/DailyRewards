package cqseur.dailyrewards.commands.debug

import cqseur.dailyrewards.utils.manager.DailyClaimManager
import cqseur.dailyrewards.utils.MessageUtils
import cqseur.dailyrewards.RewardCard
import cqseur.dailyrewards.RewardOffer
import cqseur.dailyrewards.DailyRewardsClient
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import kotlin.random.Random

object DebugCommand {
    private val logger = LoggerFactory.getLogger("[DailyRewards-Debug]")
    
    private const val DEV_UUID_RAW = "6d1c17283f5e4ea4ba64a2cebb6c6a3e"
    
    fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
            registerDebugCommands(dispatcher)
        }
    }
    
    private fun registerDebugCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommandManager.literal("dailyrewards-debug")
                .then(ClientCommandManager.literal("timezone")
                    .executes { context ->
                        showTimezoneDebug()
                        1
                    }
                )
                .then(ClientCommandManager.literal("times")
                    .executes { context ->
                        showDisplayTimes()
                        1
                    }
                )
                .then(ClientCommandManager.literal("full")
                    .executes { context ->
                        showFullDebugInfo()
                        1
                    }
                )
        )
        
        dispatcher.register(
            ClientCommandManager.literal("debugcards")
                .executes {
                    generateDebugCards()
                }
        )
    }
    
    private fun showTimezoneDebug() {
        if (!checkDeveloperAccess()) return
        
        logger.info("=== TIMEZONE DEBUG ===")
        
        val hypixelTime = DailyClaimManager.getCurrentHypixelTime()
        val localTime = DailyClaimManager.getCurrentLocalTime()
        
        MessageUtils.sendInfo("🌍 Timezone Debug:")
        MessageUtils.sendMessage("§7Hypixel (EST): §f$hypixelTime")
        MessageUtils.sendMessage("§7Your timezone: §f$localTime")
    }
    
    private fun showDisplayTimes() {
        if (!checkDeveloperAccess()) return
        
        logger.info("=== DISPLAY TIMES DEBUG ===")
        
        val displayTimes = DailyClaimManager.getDisplayTimes()
        MessageUtils.sendInfo("🕐 Current Times:")
        displayTimes.lines().forEach { line ->
            if (line.isNotBlank()) {
                MessageUtils.sendMessage("§7$line")
            }
        }
    }
    
    private fun showFullDebugInfo() {
        if (!checkDeveloperAccess()) return
        
        logger.info("=== FULL DEBUG INFO ===")
        
        val debugInfo = DailyClaimManager.getDebugInfo()
        MessageUtils.sendInfo("🔍 Full Debug Info:")
        debugInfo.lines().forEach { line ->
            if (line.isNotBlank()) {
                if (line.startsWith("===")) {
                    MessageUtils.sendMessage("§e$line")
                } else if (line.contains("🕐") || line.contains("📅") || line.contains("🔄") || line.contains("📊") || line.contains("🌍")) {
                    MessageUtils.sendMessage("§6$line")
                } else {
                    MessageUtils.sendMessage("§7$line")
                }
            }
        }
    }
    
    private fun checkDeveloperAccess(): Boolean {
        val mc = MinecraftClient.getInstance()
        val currentUuidRaw = mc.player?.uuid?.toString()?.replace("-", "") ?: ""
        
        if (currentUuidRaw != DEV_UUID_RAW) {
            MessageUtils.sendError("🚫 You are not allowed to use debug commands.")
            logger.warn("Unauthorized debug attempt from UUID: $currentUuidRaw")
            return false
        }
        
        return true
    }
    
    /**
     * Generate debug reward cards for testing
     **/
    private fun generateDebugCards(): Int {
        if (!checkDeveloperAccess()) return 0
        
        logger.info("Generating debug cards for developer")
        
        val commonOptions = listOf(
            Pair("Tung Tung Coins", "coins") to 1000..10000,
            Pair("Skibidi Toilet Dust", "dust") to 1..20,
            Pair("Ohio Gyatt Coins Fr Fr", "coins") to 250..5000
        )

        val rareOptions = listOf(
            Pair("Sigma Male Souls", "souls") to 2..10,
            Pair("BedWars Rizz XP (No Cap)", "experience") to 100..1000,
            Pair("SkyWars Mewing Tokens", "coins") to 1..5
        )

        val epicOptions = listOf(
            Pair("Hypixel Alpha Grindset XP", "experience") to 1000..5000,
            Pair("Gooning Debug Card (Sus)", "chest_open") to 1..2
        )
        
        val legendaryOptions = listOf(
            Pair("Chad Reward Token W Rizz", "adsense_token") to 1..1,
            Pair("Sussy Baka Mystery Box", "mystery_box") to 1..1
        )
        
        fun <T> pick(list: List<T>) = list[Random.nextInt(list.size)]
        
        val cards = listOf(
            generateCard(pick(commonOptions), "common"),
            generateCard(pick(commonOptions), "common"),
            generateCard(pick(rareOptions), "rare"),
            generateCard(pick(epicOptions), "epic"),
            generateCard(pick(legendaryOptions), "legendary")
        )
        
        val offer = RewardOffer("debug", cards)
        
        DailyRewardsClient.setPendingOffer(offer)
        
        MessageUtils.sendSuccess("🎁 Debug reward cards generated! Screen will open shortly.")
        logger.info("Debug cards generated successfully: ${cards.size} cards")
        
        return 1
    }
    
    /**
     * Helper to generate a reward card
     **/
    private fun generateCard(optionWithRange: Pair<Pair<String, String>, IntRange>, rarity: String): RewardCard {
        val (info, range) = optionWithRange
        val (name, icon) = info
        val amount = range.random()
        
        return RewardCard(
            name = name,
            amount = amount.toString(),
            description = "Debug $name (Generated)",
            rarity = rarity,
            iconUrl = icon
        )
    }
}
