package cqseur.dailyrewards.utils.manager

import cqseur.dailyrewards.config.ConfigManager

import org.slf4j.LoggerFactory
import java.time.*
import java.time.format.DateTimeFormatter

// Wallahi this is the worst code ever

/**
 * Manages daily claim logic with automatic timezone conversion
 * - Uses Hypixel's timezone for reset logic (midnight EST)
 * - Automatically converts times to user's local timezone for display
 **/
object DailyClaimManager {
    private val logger = LoggerFactory.getLogger("[DailyRewards-ClaimManager]")
    
    private val HYPIXEL_TIMEZONE = ZoneId.of("America/New_York")
    private val LOCAL_ZONE = ZoneId.systemDefault()
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    /**
     * Check if the player can claim rewards today
     * @return true if claim is available, false if already claimed today
     **/
    fun canClaimToday(): Boolean {
        val config = ConfigManager.config
        val currentClaimDate = getCurrentClaimDate()
        
        if (config.lastClaimDate.isEmpty()) {
            logger.info("First time claim - allowing")
            return true
        }

        val canClaim = currentClaimDate != config.lastClaimDate
        logger.debug("Can claim today: $canClaim (Current: $currentClaimDate, Last: ${config.lastClaimDate})")
        return canClaim
    }
    
    /**
     * Get the current claim date based on Hypixel's EST timezone and midnight reset time
     * @return date string in format "yyyy-MM-dd"
     **/
    private fun getCurrentClaimDate(): String {
        val now = ZonedDateTime.now(HYPIXEL_TIMEZONE)
        val resetHour = 0 // Midnight EST reset
        
        val claimDate = if (now.hour < resetHour) {
            now.minusDays(1).toLocalDate()
        } else {
            now.toLocalDate()
        }
        
        return claimDate.format(DATE_FORMAT)
    }
    
    fun getCurrentHypixelTime(): ZonedDateTime {
        return ZonedDateTime.now(HYPIXEL_TIMEZONE)
    }
    
    fun getNextResetTime(): ZonedDateTime {
        val now = ZonedDateTime.now(HYPIXEL_TIMEZONE)
        val resetHour = 0 
        
        val nextReset = now.withHour(resetHour).withMinute(0).withSecond(0).withNano(0)
        
        return if (now.isBefore(nextReset)) {
            nextReset 
        } else {
            nextReset.plusDays(1) 
        }
    }
    
    /**
     * formatted time user friendly
     **/
    fun getTimeUntilNextReset(): String {
        val now = ZonedDateTime.now(HYPIXEL_TIMEZONE)
        val nextReset = getNextResetTime()
        val duration = Duration.between(now, nextReset)
        
        val hours = duration.toHours()
        val minutes = duration.minusHours(hours).toMinutes()
        val seconds = duration.minusHours(hours).minusMinutes(minutes).toSeconds()
        
        if (hours > 1) {
            return "${hours}h ${minutes}m ${seconds}s" 
        } else {
            return "${minutes} minutes ${seconds} seconds"
        }
    }
    
    fun recordClaim() {
        val config = ConfigManager.config
        val currentClaimDate = getCurrentClaimDate()
        val currentTimestamp = System.currentTimeMillis()
        
        val yesterday = LocalDate.parse(currentClaimDate, DATE_FORMAT).minusDays(1).format(DATE_FORMAT)
        val continuesStreak = config.lastClaimDate == yesterday
        
        config.lastClaimDate = currentClaimDate
        config.lastClaimTimestamp = currentTimestamp
        config.totalClaimsCount++
        
        if (continuesStreak) {
            config.currentStreakDays++
        } else if (config.lastClaimDate.isEmpty()) {
            config.currentStreakDays = 1
        } else {
            config.currentStreakDays = 1
        }
        
        logger.info("Claim recorded for $currentClaimDate. Streak: ${config.currentStreakDays} days, Total claims: ${config.totalClaimsCount}")
        
        ConfigManager.saveConfig()
    }
    
    fun getStreakInfo(): String {
        val config = ConfigManager.config
        val currentStreak = config.currentStreakDays
        val totalClaims = config.totalClaimsCount
        
        return "$currentStreak day${if (currentStreak != 1) "s" else ""} streak | $totalClaims total claim${if (totalClaims != 1) "s" else ""}"
    }
    
    fun isStreakAtRisk(): Boolean {
        if (!canClaimToday()) return false 
        
        val config = ConfigManager.config
        if (config.currentStreakDays == 0) return false 
        
        val currentClaimDate = getCurrentClaimDate()
        val lastClaimDate = config.lastClaimDate
        
        if (lastClaimDate.isEmpty()) return false
        
        val yesterday = LocalDate.parse(currentClaimDate, DATE_FORMAT).minusDays(1).format(DATE_FORMAT)
        return lastClaimDate == yesterday
    }
    
    /**
     * Convert Hypixel timezone to user's local timezone for display
     **/
    fun convertHypixelToLocal(hypixelTime: ZonedDateTime): ZonedDateTime {
        return hypixelTime.withZoneSameInstant(LOCAL_ZONE)
    }
    
    fun getCurrentLocalTime(): ZonedDateTime {
        return getCurrentHypixelTime().withZoneSameInstant(LOCAL_ZONE)
    }
    
    fun getNextResetTimeLocal(): ZonedDateTime {
        return convertHypixelToLocal(getNextResetTime())
    }
    
    /**
     * formatted version user friendly
     **/
    fun getNextResetFullTimeFormatted(): String {
        val nextResetLocal = getNextResetTimeLocal()
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm z")
        return nextResetLocal.format(formatter)
    }
    
    fun getDisplayTimes(): String {
        val hypixelTime = getCurrentHypixelTime()
        val localTime = getCurrentLocalTime()
        val nextResetLocal = getNextResetTimeLocal()
        
        return buildString {
            appendLine("Current time (your timezone): $localTime")
            appendLine("Current time (Hypixel EST): $hypixelTime")
            appendLine("Next reset (your timezone): $nextResetLocal")
        }
    }
    
    /**
     * debug
     **/
    fun getDebugInfo(): String {
        val config = ConfigManager.config
        val currentClaimDate = getCurrentClaimDate()
        val hypixelTime = getCurrentHypixelTime()
        val localTime = getCurrentLocalTime()
        val nextResetHypixel = getNextResetTime()
        val nextResetLocal = getNextResetTimeLocal()
        val timeUntilReset = getTimeUntilNextReset()
        
        return buildString {
            appendLine("=== Daily Claim Debug Info (Multi-Timezone) ===")
            appendLine("")
            appendLine("🕐 CURRENT TIME:")
            appendLine("   Your timezone: $localTime")
            appendLine("   Hypixel (EST): $hypixelTime")
            appendLine("")
            appendLine("📅 CLAIM STATUS:")
            appendLine("   Current claim date: $currentClaimDate")
            appendLine("   Last claim date: ${config.lastClaimDate}")
            appendLine("   Can claim today: ${canClaimToday()}")
            appendLine("")
            appendLine("🔄 RESET INFO:")
            appendLine("   Next reset (your timezone): $nextResetLocal")
            appendLine("   Next reset (Hypixel EST): $nextResetHypixel")
            appendLine("   Time until reset: $timeUntilReset")
            appendLine("")
            appendLine("📊 STREAK INFO:")
            appendLine("   Current streak: ${config.currentStreakDays} days")
            appendLine("   Total claims: ${config.totalClaimsCount}")
            appendLine("   Streak at risk: ${isStreakAtRisk()}")
            appendLine("")
            appendLine("🌍 TIMEZONE INFO:")
            appendLine("   Your timezone: ${LOCAL_ZONE.id}")
            appendLine("   Hypixel timezone: ${HYPIXEL_TIMEZONE.id}")
        }
    }
}
