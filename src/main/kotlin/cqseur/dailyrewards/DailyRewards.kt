package cqseur.dailyrewards

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

class DailyRewards : ModInitializer {
    private val logger = LoggerFactory.getLogger("[DailyRewards]")
 
	override fun onInitialize() {
		logger.info("--------------------------- DailyRewards initialized ---------------------------")
	}
}