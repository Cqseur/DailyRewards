package cqseur.dailyrewards.ui

import cqseur.dailyrewards.config.ConfigManager
import cqseur.dailyrewards.utils.MessageUtils
import cqseur.dailyrewards.RewardOffer
import cqseur.dailyrewards.ModSoundEvents
import cqseur.dailyrewards.RewardClaimer
import cqseur.dailyrewards.RewardFetcher

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.client.gl.RenderPipelines
import kotlin.random.Random
import kotlin.math.roundToInt
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Formatting
import net.minecraft.text.MutableText
import net.minecraft.client.gui.Click

class RewardScreen(private val offer: RewardOffer) : Screen(Text.literal("Daily Reward")) {

    private val cardWidth = 110
    private val cardHeight = 157
    private val cardSpacing = 20
    private val ICON_SCALE = 0.75f
    private var linkAnimStartMs: Long = 0L
    private val linkAnimDurationMs: Long = 700L
    private var linkAnimFromIndex: Int = -1
    private var showMilestoneGlow: Boolean = false
    
    private var milestoneClaimAnimStart: Long = 0L
    private val milestoneClaimAnimDuration: Long = 2500L
    private var isMilestoneClaimAnim: Boolean = false

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
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.ambient(revealSound, 1f, 1f))
    }
    
    private val revealed = MutableList(offer.cards.size) { false }
    private val flipping = MutableList(offer.cards.size) { false }
    private val flipProgress = MutableList(offer.cards.size) { 0f }
    private val legendaryVariant = MutableList(offer.cards.size) { if (Random.nextBoolean()) "" else "2" }

    private val cardBackTex = Identifier.of("dailyrewards", "textures/gui/card_back.png")

    private val nodeCompletedTex = Identifier.of("dailyrewards", "textures/gui/streakbar/node_completed.png")
    private val nodeActiveTex = Identifier.of("dailyrewards", "textures/gui/streakbar/node_active.png")
    private val nodeEmptyTex = Identifier.of("dailyrewards", "textures/gui/streakbar/node_empty.png")
    private val nodePulseTex = (0..7).map { Identifier.of("dailyrewards", "textures/gui/streakbar/node_active_pulse_%02d.png".format(it)) }
    private val connectorCompletedTex = Identifier.of("dailyrewards", "textures/gui/streakbar/connector_completed.png")
    private val connectorGradientTex = Identifier.of("dailyrewards", "textures/gui/streakbar/connector_gradient.png")
    private val connectorEmptyTex = Identifier.of("dailyrewards", "textures/gui/streakbar/connector_empty.png")
    private val connectorLiquidTex = (0..9).map { Identifier.of("dailyrewards", "textures/gui/streakbar/connector_liquid_%02d.png".format(it)) }
    private val milestoneEmptyTex = Identifier.of("dailyrewards", "textures/gui/streakbar/milestone_empty.png")
    private val milestoneActiveTex = Identifier.of("dailyrewards", "textures/gui/streakbar/milestone_active.png")
    private val milestoneCompletedTex = Identifier.of("dailyrewards", "textures/gui/streakbar/milestone_completed.png")
    private val dropShadowTex = Identifier.of("dailyrewards", "textures/gui/streakbar/drop_shadow.png")
    private val borderPulseTex = (0..7).map { Identifier.of("dailyrewards", "textures/gui/streakbar/border_pulse_%02d.png".format(it)) }
    
    private val NODE_TEX_SIZE = 57  // 44 * 1.3
    private val CONNECTOR_TEX_WIDTH = 24
    private val CONNECTOR_TEX_HEIGHT = 12
    private val MILESTONE_TEX_SIZE = 110  // 44 * 2.5
    private val SHADOW_TEX_SIZE = 66  // 44 * 1.5

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        if (ConfigManager.config.showOverlay) {
            val alpha = (67.coerceIn(0,100) * 255 / 100) shl 24
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
                MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.ambient(ModSoundEvents.HOVER, 1f, 1f))
                if (!ConfigManager.config.flipAnimation) {
                    revealed[idx] = true
                    flipProgress[idx] = 1f
                    playRaritySound(card.rarity)
                } else {
                    flipping[idx] = true
                }
            }
            if (flipping[idx]) {
                flipProgress[idx] += delta * ConfigManager.config.flipSpeed 
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
            context.matrices.pushMatrix()
            context.matrices.translate(centreX, (drawY + drawH / 2f))
            val scaleXRaw = kotlin.math.cos(flipProgress[idx] * Math.PI).toFloat()
            val scaleX = kotlin.math.abs(scaleXRaw) 
            context.matrices.scale(scaleX, 1f)
            context.matrices.translate(-centreX, -(drawY + drawH / 2f))
            context.drawTexture(RenderPipelines.GUI_TEXTURED, tex, drawX, drawY, 0f, 0f, drawW, drawH, drawW, drawH)
            context.matrices.popMatrix()

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
                    context.matrices.pushMatrix()
                    context.matrices.translate(centreX, (drawY + drawH / 2f))
                    context.matrices.scale(zoom, zoom)
                    context.matrices.translate(-centreX, -(drawY + drawH / 2f))
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, glowTex, drawX, drawY, 0f, 0f, drawW, drawH, drawW, drawH)
                    context.matrices.popMatrix()
                } else {
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, glowTex, drawX, drawY, 0f, 0f, drawW, drawH, drawW, drawH)
                }

                if (card.iconUrl.isNotEmpty()) {
                    val iconTex = Identifier.of("dailyrewards", "textures/gui/icons/${card.iconUrl}.png")
                    val currentIconScale = ICON_SCALE * (if (hovered) 1.1f else 1.0f)
                    val iconW = (cardWidth * currentIconScale).toInt()
                    val iconH = (cardHeight * currentIconScale).toInt()
                    val iconX = x + (cardWidth - iconW) / 2
                    val iconY = y + (cardHeight - iconH) / 2 + 2
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, iconTex, iconX, iconY, 0f, 0f, iconW, iconH, iconW, iconH)
                }
                    val amountStr = card.amount
                    val nameStr = card.name

                    val rarityColor = when(card.rarity) {
                        "common" -> 0xFFACACAC.toInt()
                        "rare" -> 0xFF6CDBD8.toInt()
                        "epic" -> 0xFFB52ED4.toInt()
                        "legendary" -> 0xFFE0B551.toInt()
                        else -> 0xFFFFFFFF.toInt()
                    }

                    val scale = if (hovered) 1.1f else 1.0f
                context.matrices.pushMatrix()
                context.matrices.scale(scale, scale)
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
                    context.matrices.pushMatrix()
                    context.matrices.scale(nameScale, nameScale)
                    val scaledXName = (scaledX / nameScale).roundToInt()
                    val scaledYName = (nameY / nameScale).roundToInt()
                    context.drawCenteredTextWithShadow(textRenderer, Text.literal(nameStr), scaledXName, scaledYName, rarityColor)
                    context.matrices.popMatrix() */
                    context.drawCenteredTextWithShadow(textRenderer, Text.literal(amountStr), scaledX, adjustedAmountY, rarityColor)
                    context.matrices.popMatrix()

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
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(streakText), width / 2, y + cardHeight + 15, 0xFFFFFFFF.toInt())
        
        renderStreakBar(context, y + cardHeight + 35)

        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal(claimLabel).formatted(Formatting.GOLD, Formatting.BOLD),
            width / 2,
            y + cardHeight - 257,
            0xFFFFFFFF.toInt()
        )

        if (selectedIndex != null && System.currentTimeMillis() >= closeAt) {
            showMilestoneGlow = false
            MinecraftClient.getInstance().setScreen(null)
        }
    }

    private fun renderStreakBar(context: DrawContext, baseY: Int) {
        val nodeSize = 22
        val spacing = 12
        val thickness = 6
        val nodes = 9
        val totalW = nodes * nodeSize + (nodes - 1) * spacing
        val startX = (width - totalW) / 2
        val radius = nodeSize / 2
        val centerY = baseY + radius
        val centers = IntArray(nodes) { i -> startX + i * (nodeSize + spacing) + radius }

        val actualBarStep = RewardFetcher.currentBarStep
        val barPosition = actualBarStep.coerceIn(0, 8)
        
        val completedCount = barPosition
        val nextIndex = when {
            barPosition < 8 -> barPosition + 1
            barPosition == 8 -> 9
            else -> null
        }
        val now = System.currentTimeMillis()
        var animT = if (linkAnimStartMs > 0L) ((now - linkAnimStartMs).toFloat() / linkAnimDurationMs).coerceIn(0f, 1f) else -1f
        val activeGradientLeft = when {
            linkAnimStartMs > 0L && linkAnimFromIndex in 1..8 -> linkAnimFromIndex
            linkAnimStartMs > 0L && linkAnimFromIndex == 8 && nextIndex == 9 -> 8
            barPosition < 8 && completedCount > 0 -> completedCount
            else -> -1
        }

        for (i in 0 until nodes - 1) {
            val leftIndex = i + 1
            val x1 = centers[i] + radius
            val x2 = centers[i + 1] - radius
            val connectorWidth = x2 - x1
            val connectorY = centerY - thickness / 2
            
            val isConnectorToMilestone = leftIndex == 8
            val tex = when {
                activeGradientLeft == leftIndex && animT >= 0f && animT < 1f -> {
                    val frame = (animT * 9).toInt().coerceIn(0, 9)
                    connectorLiquidTex[frame]
                }
                activeGradientLeft == leftIndex -> connectorGradientTex
                leftIndex < completedCount -> connectorCompletedTex
                isConnectorToMilestone && nextIndex == 9 && barPosition == 8 -> connectorGradientTex
                else -> connectorEmptyTex
            }
            
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED, tex,
                x1, connectorY, 0f, 0f,
                connectorWidth, thickness,
                connectorWidth, thickness
            )
        }
        if (animT >= 1f) linkAnimStartMs = 0L

        for (i in 0 until 8) {
            val idx = i + 1
            val status = when {
                animT >= 0f && animT < 1f && nextIndex != null && idx == nextIndex -> 0
                idx <= completedCount -> 2
                nextIndex != null && idx == nextIndex -> 1
                else -> 0
            }
            
            val nodeDrawSize = (nodeSize * 1.3f).toInt()
            val drawX = centers[i] - nodeDrawSize / 2
            val drawY = centerY - nodeDrawSize / 2
            
            // Draw shadow
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED, dropShadowTex,
                drawX + 2, drawY + 2, 0f, 0f,
                nodeDrawSize, nodeDrawSize,
                nodeDrawSize, nodeDrawSize
            )
            
            // Draw animated border for active node
            if (status == 1) {
                val t = (now % 2000L).toFloat() / 2000f
                val frame = (t * 8).toInt().coerceIn(0, 7)
                context.drawTexture(
                    RenderPipelines.GUI_TEXTURED, borderPulseTex[frame],
                    drawX, drawY, 0f, 0f,
                    nodeDrawSize, nodeDrawSize,
                    nodeDrawSize, nodeDrawSize
                )
            }
            
            // Draw node with pulse for active status
            val nodeTex = when (status) {
                2 -> nodeCompletedTex
                1 -> {
                    val t = (now % 1500L).toFloat() / 1500f
                    val frame = (t * 8).toInt().coerceIn(0, 7)
                    nodePulseTex[frame]
                }
                else -> nodeEmptyTex
            }
            
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED, nodeTex,
                drawX, drawY, 0f, 0f,
                nodeDrawSize, nodeDrawSize,
                nodeDrawSize, nodeDrawSize
            )
            
            val textColor = if (status == 0) 0xFFB0B0B0.toInt() else 0xFFFFFFFF.toInt()
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(idx.toString()), centers[i], centerY - 4, textColor)
        }
        
        if (isMilestoneClaimAnim) {
            renderMilestoneClaimAnimation(context, centerY, radius)
        }

        val milestoneStatus = when {
            animT >= 0f && animT < 1f && nextIndex == 9 -> 0
            showMilestoneGlow -> 2
            actualBarStep >= 9 -> 2
            barPosition == 8 -> 1
            nextIndex == 9 -> 1
            else -> 0
        }
        val mCenterX = centers[8]
        
        // Draw milestone glow effect
        if (showMilestoneGlow && !isMilestoneClaimAnim) {
            val glowTex = Identifier.of("dailyrewards", "textures/gui/glow_legendary2.png")
            val t = (now % 2000L).toFloat() / 2000f
            val scaleVariation = 1f + 0.1f * kotlin.math.sin(t * 2f * Math.PI).toFloat()
            val rotationAngle = t * 360f
            
            val glowSize = (radius * 5f).toInt()
            val glowDrawX = mCenterX - glowSize / 2
            val glowDrawY = centerY - glowSize / 2
            
            context.matrices.pushMatrix()
            context.matrices.translate(mCenterX.toFloat(), centerY.toFloat())
            context.matrices.scale(scaleVariation, scaleVariation)
            context.matrices.rotate(Math.toRadians(rotationAngle.toDouble()).toFloat())
            context.matrices.translate(-mCenterX.toFloat(), -centerY.toFloat())
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED, glowTex,
                glowDrawX, glowDrawY, 0f, 0f,
                glowSize, glowSize,
                glowSize, glowSize
            )
            context.matrices.popMatrix()
        }
        
        // Draw milestone gem using texture with pulse for active state
        val milestoneTex = when (milestoneStatus) {
            2 -> milestoneCompletedTex
            1 -> milestoneActiveTex
            else -> milestoneEmptyTex
        }
        val baseDrawSize = (nodeSize * 2.5f).toInt()
        
        // Pulse animation for active milestone
        val pulseScale = if (milestoneStatus == 1) {
            val t = (now % 1500L).toFloat() / 1500f
            1f + 0.08f * kotlin.math.sin(t * 2f * Math.PI).toFloat()
        } else 1f
        
        val milestoneDrawSize = (baseDrawSize * pulseScale).toInt()
        val milestoneDrawX = mCenterX - milestoneDrawSize / 2
        val milestoneDrawY = centerY - milestoneDrawSize / 2
        
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED, milestoneTex,
            milestoneDrawX, milestoneDrawY, 0f, 0f,
            milestoneDrawSize, milestoneDrawSize,
            milestoneDrawSize, milestoneDrawSize
        )
    }

    override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
        val mouseX = click.x
        val mouseY = click.y
        
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
                    MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.ambient(ModSoundEvents.PICK, 1f, 1f))
                    if (offer.id != "debug") {
                        RewardClaimer.claim(idx, offer.id)
                    }
                    val mc = MinecraftClient.getInstance()
                    val card = offer.cards[idx]
                    val rarityFormat = rarityToFormat(card.rarity)
                    val msg: MutableText = MessageUtils.PREFIX()
                        .formatted(Formatting.WHITE)
                        .append(Text.literal("Claiming reward #${idx + 1}: ").formatted(Formatting.RESET))
                        .append(Text.literal("${card.rarity.uppercase()} ").formatted(rarityFormat, Formatting.BOLD))
                        .append(Text.literal(card.name).formatted(rarityFormat))
                        .append(Text.literal(" x${card.amount}").formatted(Formatting.RESET))

                    mc.player?.sendMessage(msg, false)

                    val prevBarPosition = RewardFetcher.currentBarStep
                    val newBarPosition = prevBarPosition + 1
                    
                    if (newBarPosition <= 8) {
                        linkAnimFromIndex = newBarPosition
                        linkAnimStartMs = System.currentTimeMillis()
                    } else if (prevBarPosition == 8) {
                        isMilestoneClaimAnim = true
                        milestoneClaimAnimStart = System.currentTimeMillis()
                        linkAnimFromIndex = 8
                        linkAnimStartMs = System.currentTimeMillis()
                        showMilestoneGlow = true
                    }

                    RewardFetcher.currentStreak = RewardFetcher.currentStreak + 1
                    if (RewardFetcher.currentStreak > RewardFetcher.highestStreak) {
                        RewardFetcher.highestStreak = RewardFetcher.currentStreak
                    }
                    RewardFetcher.currentBarStep = newBarPosition

                    claimLabel = "Reward Claimed, comeback tomorrow for more rewards!"
                    selectedIndex = idx
                    initialX = startX + idx * (cardWidth + cardSpacing)
                    moveStart = System.currentTimeMillis()
                    closeAt = System.currentTimeMillis() + 2000
                    return true
                }
            }
        }
        return super.mouseClicked(click, doubled)
    }

    override fun shouldPause(): Boolean = false

    private fun renderMilestoneClaimAnimation(context: DrawContext, centerY: Int, radius: Int) {
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - milestoneClaimAnimStart
        val animProgress = (elapsed.toFloat() / milestoneClaimAnimDuration).coerceIn(0f, 1f)
        
        val textProgress = animProgress
        val smoothText = smoothstep(textProgress)
        
        if (textProgress > 0f) {
            val text = Text.literal("§6+1 Daily Reward Token")
            val textWidth = textRenderer.getWidth(text)
            val textX = width / 2 - textWidth / 2
            val startY = 390
            val endY = 415
            val textY = startY + (endY - startY) * smoothText
            
            val textAlpha = (255f * textProgress).toInt()
            val textColor = 0x00FFAA00 or (textAlpha shl 24)
            
            context.drawTextWithShadow(textRenderer, text, textX, textY.toInt(), textColor)
        }
        
        if (animProgress >= 1f) {
            isMilestoneClaimAnim = false
        }
    }
    
    private fun smoothstep(t: Float): Float = t * t * (3f - 2f * t)
}
