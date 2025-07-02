package cqseur.dailyrewards.ui

import cqseur.dailyrewards.config.DailyRewardsConfig
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.client.MinecraftClient

object DailyRewardsConfigScreen {
    fun create(parent: Screen?): Screen {
        val builder: ConfigBuilder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.literal("DailyRewards Settings"))

        val eb = builder.entryBuilder()
        val general: ConfigCategory = builder.getOrCreateCategory(Text.literal("General"))

        general.addEntry(
            eb.startBooleanToggle(Text.literal("Card flip animation"), DailyRewardsConfig.flipAnimation)
                .setSaveConsumer { DailyRewardsConfig.flipAnimation = it }
                .build()
        )

        general.addEntry(
            eb.startFloatField(Text.literal("Flip speed"), DailyRewardsConfig.flipSpeed)
                .setMin(0.5f).setMax(4.0f)
                .setSaveConsumer { DailyRewardsConfig.flipSpeed = it }
                .build()
        )

        val devUuid = "6d1c17283f5e4ea4ba64a2cebb6c6a3e"
        val devUuidRaw = "6d1c17283f5e4ea4ba64a2cebb6c6a3e"
        val currentUuidRaw = MinecraftClient.getInstance().player?.uuid?.toString()?.replace("-", "") ?: ""
        if (currentUuidRaw == devUuidRaw) {
            val dev: ConfigCategory = builder.getOrCreateCategory(Text.literal("Developer"))
            dev.addEntry(
                eb.startBooleanToggle(Text.literal("Debug mode"), DailyRewardsConfig.debugMode)
                    .setSaveConsumer { DailyRewardsConfig.debugMode = it }
                    .build()
            )
            dev.addEntry(
                eb.startBooleanToggle(Text.literal("Show overlay"), DailyRewardsConfig.showOverlay)
                    .setSaveConsumer { DailyRewardsConfig.showOverlay = it }
                    .build()
            )
            dev.addEntry(
                eb.startIntSlider(Text.literal("Overlay opacity (%)"), DailyRewardsConfig.overlayOpacityPercent, 0, 100)
                    .setSaveConsumer { DailyRewardsConfig.overlayOpacityPercent = it }
                    .build()
            )
        }

        builder.setSavingRunnable { DailyRewardsConfig.save() }
        return builder.build()
    }
}
