package cqseur.dailyrewards.ui

import cqseur.dailyrewards.config.ConfigManager
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.client.Minecraft

object DailyRewardsConfigScreen {
    fun create(parent: Screen?): Screen {
        val builder: ConfigBuilder = ConfigBuilder.create().setTitle(Component.literal("DailyRewards"))
            .setParentScreen(parent)
            builder.setGlobalized(true)
            builder.setGlobalizedExpanded(false)

        val eb = builder.entryBuilder()

        val generalSettings = builder.getOrCreateCategory(Component.literal("Settings"))
        
        generalSettings.addEntry(
            eb.startBooleanToggle(Component.literal("Enable Daily Rewards"), ConfigManager.config.modEnabled)
                .setSaveConsumer { ConfigManager.config.modEnabled = it }
                .build()
        )

        generalSettings.addEntry(
            eb.startBooleanToggle(Component.literal("Daily Reminder"), ConfigManager.config.dailyReminder)
                .setTooltip(Component.literal("Are you assisted and need a reminder ? Great ! Me too so there it is !"))
                .setSaveConsumer { ConfigManager.config.dailyReminder = it }
                .build()
        )

        val cardSettings = builder.getOrCreateCategory(Component.literal("Card Settings"))
        cardSettings.addEntry(
            eb.startBooleanToggle(Component.literal("Card flip animation"), ConfigManager.config.flipAnimation)
                .setSaveConsumer { ConfigManager.config.flipAnimation = it }
                .build()
        )

        cardSettings.addEntry(
            eb.startFloatField(Component.literal("Flip speed"), ConfigManager.config.flipSpeed)
                .setMin(0.1f).setMax(3.0f)
                .setSaveConsumer { ConfigManager.config.flipSpeed = it }
                .build()
        )

        val devUuidRaw = "6d1c17283f5e4ea4ba64a2cebb6c6a3e"
        val currentUuidRaw = Minecraft.getInstance().player?.uuid?.toString()?.replace("-", "") ?: ""
        if (currentUuidRaw == devUuidRaw) {
            val dev: ConfigCategory = builder.getOrCreateCategory(Component.literal("Developer"))
            dev.addEntry(
                eb.startBooleanToggle(Component.literal("Show overlay"), ConfigManager.config.showOverlay)
                    .setSaveConsumer { ConfigManager.config.showOverlay = it }
                    .build()
            )
        }

        builder.setSavingRunnable { ConfigManager.saveConfig() }
        return builder.build()
    }
}
