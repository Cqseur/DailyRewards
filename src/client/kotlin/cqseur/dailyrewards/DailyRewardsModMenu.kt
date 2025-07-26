package cqseur.dailyrewards

import com.terraformersmc.modmenu.api.ModMenuApi
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import net.minecraft.client.gui.screen.Screen
import cqseur.dailyrewards.ui.DailyRewardsConfigScreen
<<<<<<< Updated upstream

object DailyRewardsModMenu : ModMenuApi {
=======
 
class DailyRewardsModMenu : ModMenuApi {
>>>>>>> Stashed changes
    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> {
        return ConfigScreenFactory { parent: Screen? ->
            DailyRewardsConfigScreen.create(parent)
        }
    }
} 