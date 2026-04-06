package cqseur.dailyrewards.ui

import cqseur.dailyrewards.config.ConfigManager
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.client.MinecraftClient

object DailyRewardsConfigScreen {
    fun create(parent: Screen?): Screen {
        val builder: ConfigBuilder = ConfigBuilder.create().setTitle(Text.literal("DailyRewards"))
            .setParentScreen(parent)
            builder.setGlobalized(true)
            builder.setGlobalizedExpanded(false)

        val eb = builder.entryBuilder()

        val generalSettings = builder.getOrCreateCategory(Text.literal("Settings"))
        
        generalSettings.addEntry(
            eb.startBooleanToggle(Text.literal("Enable Daily Rewards"), ConfigManager.config.modEnabled)
                .setSaveConsumer { ConfigManager.config.modEnabled = it }
                .build()
        )

        generalSettings.addEntry(
            eb.startBooleanToggle(Text.literal("Daily Reminder"), ConfigManager.config.dailyReminder)
                .setTooltip(Text.literal("Are you assisted and need a reminder ? Great ! Me too so there it is !"))
                .setSaveConsumer { ConfigManager.config.dailyReminder = it }
                .build()
        )

        val cardSettings = builder.getOrCreateCategory(Text.literal("Card Settings"))
        cardSettings.addEntry(
            eb.startBooleanToggle(Text.literal("Card flip animation"), ConfigManager.config.flipAnimation)
                .setSaveConsumer { ConfigManager.config.flipAnimation = it }
                .build()
        )

        cardSettings.addEntry(
            eb.startFloatField(Text.literal("Flip speed"), ConfigManager.config.flipSpeed)
                .setMin(0.1f).setMax(3.0f)
                .setSaveConsumer { ConfigManager.config.flipSpeed = it }
                .build()
        )

        val devUuidRaw = "6d1c17283f5e4ea4ba64a2cebb6c6a3e"
        val currentUuidRaw = MinecraftClient.getInstance().player?.uuid?.toString()?.replace("-", "") ?: ""
        if (currentUuidRaw == devUuidRaw) {
            val dev: ConfigCategory = builder.getOrCreateCategory(Text.literal("Developer"))
            dev.addEntry(
                eb.startBooleanToggle(Text.literal("Show overlay"), ConfigManager.config.showOverlay)
                    .setSaveConsumer { ConfigManager.config.showOverlay = it }
                    .build()
            )
        }

        builder.setSavingRunnable { ConfigManager.saveConfig() }
        return builder.build()
    }
}
