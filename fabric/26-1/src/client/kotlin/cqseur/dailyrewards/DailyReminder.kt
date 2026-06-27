package cqseur.dailyrewards

import cqseur.dailyrewards.config.ConfigManager
import cqseur.dailyrewards.utils.MessageUtils
import cqseur.dailyrewards.utils.manager.DailyClaimManager

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundEvents
import org.slf4j.LoggerFactory

object DailyReminder {
    private val logger = LoggerFactory.getLogger("[DailyRewards-Reminder]")
    private var isOnHypixel = false
    private var lastServerAddress: String? = null
    private var reminderSent = false
    private var ticksSinceJoin = 0
    
    private val REMINDER_DELAY_TICKS = 20 * 5 
    private val REMINDER_INTERVAL_TICKS = 20 * 60 * 5
    private var lastReminderTick = 0
    
    fun init() {
        logger.info("Initializing DailyReminder system")
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            onClientTick(client)
        }
    }
    
    private fun onClientTick(client: Minecraft) {
        if (!ConfigManager.config.dailyReminder) return
        
        if (!DailyClaimManager.canClaimToday()) return

        val currentServerAddress = getCurrentServerAddress(client)
        val currentlyOnHypixel = isHypixelServer(currentServerAddress)
        
        if (currentServerAddress != lastServerAddress) {
            onServerChange(currentServerAddress, currentlyOnHypixel)
        }
        
        if (currentlyOnHypixel) {
            ticksSinceJoin++
            handleHypixelReminder()
        }
        
        lastServerAddress = currentServerAddress
        isOnHypixel = currentlyOnHypixel
    }
    
    private fun getCurrentServerAddress(client: Minecraft): String? {
        return client.currentServer?.ip?.lowercase()
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
    
    private fun handleHypixelReminder() {
        if (!reminderSent && ticksSinceJoin >= REMINDER_DELAY_TICKS) {
            sendInitialReminder()
            reminderSent = true
            lastReminderTick = ticksSinceJoin
        }
        
        if (reminderSent && (ticksSinceJoin - lastReminderTick) >= REMINDER_INTERVAL_TICKS) {
            sendPeriodicReminder()
            lastReminderTick = ticksSinceJoin
        }
    }
    
    private fun sendInitialReminder() {
        val client = Minecraft.getInstance()
        val player = client.player
        client.execute {
            MessageUtils.sendWarning("⭐ §6Don't forget to claim your daily rewards!")
            player?.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1.0f, 1.0f)
        }
    }
    
    private fun sendPeriodicReminder() {
        val client = Minecraft.getInstance()
        val player = client.player
        client.execute {
            MessageUtils.sendWarning("⌚ §6Reminder: You still haven't claimed your daily rewards !")
            player?.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1.0f, 1.0f)
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
