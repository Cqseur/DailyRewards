package cqseur.dailyrewards

import cqseur.dailyrewards.config.ConfigManager
import cqseur.dailyrewards.utils.MessageUtils
import cqseur.dailyrewards.utils.manager.DailyClaimManager

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Formatting
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.Timer

object DailyReminder {
    private val logger = LoggerFactory.getLogger("[DailyRewards-Reminder]")
    private var isOnHypixel = false
    private var lastServerAddress: String? = null
    private var reminderSent = false
    private var ticksSinceJoin = 0
    
    private val REMINDER_DELAY_TICKS = 20 * 5 
    private val REMINDER_INTERVAL_TICKS = 20 * 60 * 10 
    private var lastReminderTick = 0
    
    fun init() {
        logger.info("Initializing DailyReminder system")
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            onClientTick(client)
        }
    }
    
    private fun onClientTick(client: MinecraftClient) {
        if (!ConfigManager.config.dailyReminder) return
        
        if (!DailyClaimManager.canClaimToday()) return 
        
        val player = client.player ?: return
        val world = client.world ?: return
        
        val currentServerAddress = getCurrentServerAddress(client)
        val currentlyOnHypixel = isHypixelServer(currentServerAddress)
        
        if (currentServerAddress != lastServerAddress) {
            onServerChange(currentServerAddress, currentlyOnHypixel)
        }
        
        if (currentlyOnHypixel) {
            ticksSinceJoin++
            handleHypixelReminder(client)
        }
        
        lastServerAddress = currentServerAddress
        isOnHypixel = currentlyOnHypixel
    }
    
    private fun getCurrentServerAddress(client: MinecraftClient): String? {
        return client.currentServerEntry?.address?.lowercase()
    }
    
    private fun isHypixelServer(address: String?): Boolean {
        if (address == null) return false
        return address.contains("hypixel") || 
               address.contains("mc.hypixel.net") ||
               address.contains("hypixel.io")
    }
    
    private fun onServerChange(newAddress: String?, isHypixel: Boolean) {
        if (isHypixel && newAddress != null) {
            logger.info("Joined Hypixel server: $newAddress")
            ticksSinceJoin = 0
            reminderSent = false
            lastReminderTick = 0
        } else if (!isHypixel && isOnHypixel) {
            logger.info("Left Hypixel server")
            reminderSent = false
        }
    }
    
    private fun handleHypixelReminder(client: MinecraftClient) {
        val currentTick = client.world?.time?.toInt() ?: 0
        
        if (!reminderSent && ticksSinceJoin >= REMINDER_DELAY_TICKS) {
            sendInitialReminder()
            reminderSent = true
            lastReminderTick = currentTick
        }
        
        if (reminderSent && (currentTick - lastReminderTick) >= REMINDER_INTERVAL_TICKS) {
            sendPeriodicReminder()
            lastReminderTick = currentTick
        }
    }
    
    private fun sendInitialReminder() {
        val client = MinecraftClient.getInstance()
        val player = client.player
        client.execute {
            MessageUtils.sendWarning("⭐ §6Don't forget to claim your daily rewards!")
            player?.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0f, 1.0f)
        }
    }
    
    private fun sendPeriodicReminder() {
        val client = MinecraftClient.getInstance()
        val player = client.player
        client.execute {
            MessageUtils.sendWarning("⌚ §6Reminder: You still haven't claimed your daily rewards !")
            player?.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0f, 1.0f)
        }
    }
    
    /**
     * debug
     **/
    fun triggerReminder() {
        if (isOnHypixel) {
            sendInitialReminder()
        } else {
            return
        }
    }

    fun isCurrentlyOnHypixel(): Boolean = isOnHypixel
    
    /**
     * debug
     **/
    fun resetReminderState() {
        reminderSent = false
        ticksSinceJoin = 0
        lastReminderTick = 0
        logger.info("Reminder state reset")
    }
}
