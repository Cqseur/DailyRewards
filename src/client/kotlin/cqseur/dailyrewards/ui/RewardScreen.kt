package cqseur.dailyrewards.ui

import cqseur.dailyrewards.config.DailyRewardsConfig
import cqseur.dailyrewards.RewardOffer
import cqseur.dailyrewards.ModSoundEvents
import cqseur.dailyrewards.RewardClaimer
import cqseur.dailyrewards.RewardFetcher

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.client.render.RenderLayer
import com.mojang.blaze3d.systems.RenderSystem
import kotlin.random.Random
import kotlin.math.roundToInt
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Formatting
import net.minecraft.text.MutableText
import net.minecraft.util.math.RotationAxis


class RewardScreen(private val offer: RewardOffer) : Screen(Text.literal("Daily Reward")) {

    private val cardWidth = 110
    private val cardHeight = 157
    private val cardSpacing = 20
    private val ICON_SCALE = 0.75f

    private var claimLabel: String = "Choose One.One.One.One card to claim your reward"
    private var selectedIndex: Int? = null
    private var closeAt: Long = 0
    private var moveStart: Long = 0
    private var initialX: Int = 0

    private fun rarityToFormat(rarity: String): Formatting = when(rarity) {
        "common" -> Formatting.GRAY
        "rare" -> Formatting.AQUA
        "epic" -> Formatting.LIGHT_PURPLE
        "legendary" -> Formatting.GOLD
        else -> Formatting.WHITE
    }
    
    private fun playRaritySound(rarity: String) {
        val revealSound: SoundEvent = when(rarity) {
            "common" -> ModSoundEvents.COMMON
            "rare" -> ModSoundEvents.RARE
            "epic" -> ModSoundEvents.EPIC
            "legendary" -> ModSoundEvents.LEGENDARY
            else -> ModSoundEvents.COMMON
        }
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(revealSound, 1f))
    }
    
    private val revealed = MutableList(offer.cards.size) { false }
    private val flipping = MutableList(offer.cards.size) { false }
    private val flipProgress = MutableList(offer.cards.size) { 0f }
    private val legendaryVariant = MutableList(offer.cards.size) { if (Random.nextBoolean()) "" else "2" }

    private val cardBackTex = Identifier.of("dailyrewards", "textures/gui/card_back.png")

    

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        this.renderBackground(context, mouseX, mouseY, delta)

        if (DailyRewardsConfig.showOverlay) {
            val alpha = (DailyRewardsConfig.overlayOpacityPercent.coerceIn(0,100) * 255 / 100) shl 24
            context.fill(0, 0, width, height, alpha)
        }

        val totalWidth = offer.cards.size * cardWidth + (offer.cards.size - 1) * cardSpacing
        val startX = (width - totalWidth) / 2
        val y = (height - cardHeight) / 2

        offer.cards.forEachIndexed { idx, card ->
            if (selectedIndex != null && idx != selectedIndex) return@forEachIndexed

            var xDynamic = startX + idx * (cardWidth + cardSpacing)
            if (selectedIndex == idx) {
                val targetX = (width - cardWidth) / 2
                val progress = if (moveStart == 0L) 1f else ((System.currentTimeMillis() - moveStart).coerceAtLeast(0) / 400f).coerceIn(0f, 1f)
                xDynamic = (initialX + ((targetX - initialX) * progress)).toInt()
            }

            val x = xDynamic
            val hovered = mouseX in x..(x + cardWidth) && mouseY in y..(y + cardHeight)
            if (hovered && !revealed[idx] && !flipping[idx]) {
                MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(ModSoundEvents.HOVER, 1f))
                if (!DailyRewardsConfig.flipAnimation) {
                    revealed[idx] = true
                    flipProgress[idx] = 1f
                    playRaritySound(card.rarity)
                } else {
                    flipping[idx] = true
                }
            }
            if (flipping[idx]) {
                flipProgress[idx] += delta * DailyRewardsConfig.flipSpeed 
                if (flipProgress[idx] >= 1f) {
                    flipProgress[idx] = 1f
                    flipping[idx] = false
                    revealed[idx] = true
                    playRaritySound(card.rarity)
                }
            }
            val tex: Identifier = if (flipProgress[idx] < 0.5f) cardBackTex else Identifier.of("dailyrewards", "textures/gui/card_${card.rarity}.png")
            val drawW = if (hovered) (cardWidth * 1.1).toInt() else cardWidth
            val drawH = if (hovered) (cardHeight * 1.1).toInt() else cardHeight
            val drawX = x - (drawW - cardWidth) / 2
            val drawY = y - (drawH - cardHeight) / 2
            val centreX = drawX + drawW / 2f
            context.matrices.push()
            context.matrices.translate(centreX, (drawY + drawH / 2f), 0f)
            val scaleXRaw = kotlin.math.cos(flipProgress[idx] * Math.PI).toFloat()
            val scaleX = kotlin.math.abs(scaleXRaw) 
            context.matrices.scale(scaleX, 1f, 1f)
            context.matrices.translate(-centreX, -(drawY + drawH / 2f), 0f)
            context.drawTexture(RenderLayer::getGuiTextured, tex, drawX, drawY, 0f, 0f, drawW, drawH, drawW, drawH)
            context.matrices.pop()

            if (flipProgress[idx] >= 0.5f) {
                val isLegendary = card.rarity == "legendary"
                val glowTex: Identifier = if (isLegendary) {
                    Identifier.of("dailyrewards", "textures/gui/glow_legendary${legendaryVariant[idx]}.png")
                } else {
                    Identifier.of("dailyrewards", "textures/gui/glow_${card.rarity}.png")
                }
                if (isLegendary) {
                    val t = (System.currentTimeMillis() % 2000L).toFloat() / 2000f 
                    val zoom = 1f + 0.05f * kotlin.math.sin(t * 2f * Math.PI).toFloat()
                    context.matrices.push()
                    context.matrices.translate(centreX, (drawY + drawH / 2f), 0f)
                    context.matrices.scale(zoom, zoom, 1f)
                    context.matrices.translate(-centreX, -(drawY + drawH / 2f), 0f)
                    context.drawTexture(RenderLayer::getGuiTextured, glowTex, drawX, drawY, 0f, 0f, drawW, drawH, drawW, drawH)
                    context.matrices.pop()
                } else {
                    context.drawTexture(RenderLayer::getGuiTextured, glowTex, drawX, drawY, 0f, 0f, drawW, drawH, drawW, drawH)
                }

                if (card.iconUrl.isNotEmpty()) {
                    val iconTex = Identifier.of("dailyrewards", "textures/gui/icons/${card.iconUrl}.png")
                    val currentIconScale = ICON_SCALE * (if (hovered) 1.1f else 1.0f)
                    val iconW = (cardWidth * currentIconScale).toInt()
                    val iconH = (cardHeight * currentIconScale).toInt()
                    val iconX = x + (cardWidth - iconW) / 2
                    val iconY = y + (cardHeight - iconH) / 2 + 2
                    context.drawTexture(RenderLayer::getGuiTextured, iconTex, iconX, iconY, 0f, 0f, iconW, iconH, iconW, iconH)
                }
                    val amountStr = card.amount
                    val nameStr = card.name

                    val rarityColor = when(card.rarity) {
                        "common" -> 0xACACAC
                        "rare" -> 0x6CDBD8
                        "epic" -> 0xB52ED4
                        "legendary" -> 0xE0B551
                        else -> 0xFFFFFF
                    }

                    val scale = if (hovered) 1.1f else 1.0f
                context.matrices.push()
                context.matrices.scale(scale, scale, 1f)
                val scaledX = ((x + cardWidth / 2).toFloat() / scale).roundToInt()
                val nameBase = y + cardHeight - 45 + if (hovered) 4 else 0
                val nameY = (nameBase.toFloat() / scale).roundToInt()
                val amountBase = y + cardHeight - 25 + if (hovered) 4 else 0
                val amountY = (amountBase.toFloat() / scale).roundToInt()
                    //---- wrap long names onto two lines ----//
                    val maxNameWidth = cardWidth - 10 
                    var firstLine = nameStr
                    var secondLine: String? = null
                    if (textRenderer.getWidth(nameStr) > maxNameWidth && nameStr.contains(" ")) {
                        val words = nameStr.split(" ")
                        var line = ""
                        var index = 0
                        while (index < words.size) {
                            val candidate = if (line.isEmpty()) words[index] else "$line ${words[index]}"
                            if (textRenderer.getWidth(candidate) <= maxNameWidth) {
                                line = candidate
                                index++
                            } else {
                                break
                            }
                        }
                        firstLine = line
                        secondLine = words.subList(index, words.size).joinToString(" ")
                    }
                    var adjustedNameY = nameY
                    var adjustedAmountY = amountY
                    if (secondLine != null) {
                        adjustedNameY -= 7
                    }
                    context.drawCenteredTextWithShadow(textRenderer, Text.literal(firstLine), scaledX, adjustedNameY, rarityColor)
                    if (secondLine != null) {
                        context.drawCenteredTextWithShadow(textRenderer, Text.literal(secondLine), scaledX, adjustedNameY + 10, rarityColor)
                    }
                    /*
                    // downscale name to fit 
                    val nameWidthPx = textRenderer.getWidth(nameStr)
                    val maxNameWidth = cardWidth - 10 
                    val nameScale = if (nameWidthPx > maxNameWidth) maxNameWidth.toFloat() / nameWidthPx else 1f
                    context.matrices.push()
                    context.matrices.scale(nameScale, nameScale, 1f)
                    val scaledXName = (scaledX / nameScale).roundToInt()
                    val scaledYName = (nameY / nameScale).roundToInt()
                    context.drawCenteredTextWithShadow(textRenderer, Text.literal(nameStr), scaledXName, scaledYName, rarityColor)
                    context.matrices.pop() */
                    context.drawCenteredTextWithShadow(textRenderer, Text.literal(amountStr), scaledX, adjustedAmountY, rarityColor)
                    context.matrices.pop()

                    if (hovered) {
                        val rarityFormat = rarityToFormat(card.rarity)
                            val tooltip = listOf(
                                Text.literal("Rarity: ")
                                    .append(Text.literal(card.rarity.uppercase())
                                        .formatted(rarityFormat, Formatting.BOLD)),
                                Text.literal(card.description)
                            )
                        context.drawTooltip(textRenderer, tooltip, mouseX, mouseY)
                    }
            }
        }

        val streakText = "Daily Streak: ${RewardFetcher.currentStreak}    Highest Streak: ${RewardFetcher.highestStreak}"
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(streakText), width / 2, y + cardHeight + 15, 0xFFFFFF)

        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal(claimLabel).formatted(Formatting.GOLD, Formatting.BOLD),
            width / 2,
            y + cardHeight - 257,
            0xFFFFFF
        )

        if (selectedIndex != null && System.currentTimeMillis() >= closeAt) {
            MinecraftClient.getInstance().setScreen(null)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (selectedIndex != null) return true
        val totalWidth = offer.cards.size * cardWidth + (offer.cards.size - 1) * cardSpacing
        val startX = (width - totalWidth) / 2
        val y = (height - cardHeight) / 2

        offer.cards.forEachIndexed { idx, _ ->
            val x = startX + idx * (cardWidth + cardSpacing)
            if (mouseX >= x && mouseX <= x + cardWidth && mouseY >= y && mouseY <= y + cardHeight) {
                if (!revealed[idx]) {
                    return true
                } else {

                MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(ModSoundEvents.PICK, 1f))
                if (offer.id != "debug") {
                RewardClaimer.claim(idx, offer.id)
            }
                val mc = MinecraftClient.getInstance()
                val card = offer.cards[idx]
                val rarityFormat = rarityToFormat(card.rarity)
                val msg: MutableText = Text.literal(DailyRewardsConfig.PREFIX)
                    .formatted(Formatting.WHITE)
                    .append(Text.literal("Claiming reward #${idx + 1}: ").formatted(Formatting.RESET))
                    .append(Text.literal("${card.rarity.uppercase()} ").formatted(rarityFormat, Formatting.BOLD))
                    .append(Text.literal(card.name).formatted(rarityFormat))
                    .append(Text.literal(" x${card.amount}").formatted(Formatting.RESET))

                mc.player?.sendMessage(msg, false)

                RewardFetcher.currentStreak += 1
                if (RewardFetcher.currentStreak > RewardFetcher.highestStreak) {
                    RewardFetcher.highestStreak = RewardFetcher.currentStreak
                }

                 claimLabel = "Reward Claimed, comeback tomorrow for more rewards!"
                selectedIndex = idx
                    initialX = startX + idx * (cardWidth + cardSpacing)
                moveStart = System.currentTimeMillis()
                closeAt = System.currentTimeMillis() + 2000
                return true
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun shouldPause(): Boolean = false
}
