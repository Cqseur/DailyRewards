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
import net.minecraft.client.render.RenderLayer
import kotlin.random.Random
import kotlin.math.roundToInt
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Formatting
import net.minecraft.text.MutableText

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
                MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(ModSoundEvents.HOVER, 1f))
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
        
        renderStreakBar(context, y + cardHeight + 35)

        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal(claimLabel).formatted(Formatting.GOLD, Formatting.BOLD),
            width / 2,
            y + cardHeight - 257,
            0xFFFFFF
        )

        if (selectedIndex != null && System.currentTimeMillis() >= closeAt) {
            showMilestoneGlow = false
            MinecraftClient.getInstance().setScreen(null)
        }
    }

    private fun renderStreakBar(context: DrawContext, baseY: Int) {
        val nodeSize = 22.coerceAtLeast(6)
        val spacing = 12.coerceAtLeast(4)
        val thickness = 6.coerceAtLeast(2)
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
            if (activeGradientLeft == leftIndex) {
                if (animT >= 0f && animT < 1f) {
                    drawLiquidConnector(context, x1, x2, centerY, thickness, animT, 0xFF4CAF50.toInt(), 0xFFE0B551.toInt(), 0xFF3A3A3A.toInt())
                } else {
                    drawEnhanced3DConnector(context, x1, x2, centerY, thickness, 0xFF4CAF50.toInt(), 0xFFE0B551.toInt())
                }
            } else if (leftIndex < completedCount) {
                drawEnhanced3DConnector(context, x1, x2, centerY, thickness, 0xFF4CAF50.toInt(), 0xFF4CAF50.toInt())
            } else if (leftIndex == 8 && barPosition == 8) {
                drawEnhanced3DConnector(context, x1, x2, centerY, thickness, 0xFF4CAF50.toInt(), 0xFF4CAF50.toInt())
            } else {
                drawEnhanced3DConnector(context, x1, x2, centerY, thickness, 0xFF3A3A3A.toInt(), 0xFF3A3A3A.toInt())
            }
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
            val fillColor = when (status) {
                2 -> 0xFF4CAF50.toInt()
                1 -> 0xFFE0B551.toInt()
                else -> 0xFF3A3A3A.toInt()
            }
            drawDropShadow(context, centers[i].toFloat(), centerY.toFloat(), radius.toFloat())
            
            if (status == 1) {
                drawAnimatedBorder(context, centers[i].toFloat(), centerY.toFloat(), radius.toFloat())
            }
            
            val pulseScale = if (status == 1) {
                val t = (System.currentTimeMillis() % 1500L).toFloat() / 1500f
                1f + 0.1f * kotlin.math.sin(t * 2f * Math.PI).toFloat()
            } else 1f
            
            drawEnhancedNode(context, centers[i].toFloat(), centerY.toFloat(), radius.toFloat() * pulseScale, fillColor, status)
            
            val textColor = if (status == 0) 0xFFB0B0B0.toInt() else 0xFFFFFFFF.toInt()
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(idx.toString()), centers[i], centerY - 4, textColor)
        }
        
        if (isMilestoneClaimAnim) {
            renderMilestoneClaimAnimation(context, centerY, radius)
        }

        val milestoneFill = when {
            animT >= 0f && animT < 1f && nextIndex == 9 -> 0xFF3A3A3A.toInt()
            showMilestoneGlow -> 0xFF4CAF50.toInt()
            actualBarStep >= 9 -> 0xFF4CAF50.toInt()
            barPosition == 8 -> 0xFFE0B551.toInt() 
            nextIndex == 9 -> 0xFFE0B551.toInt()
            else -> 0xFF3A3A3A.toInt()
        }
        val mCenterX = centers[8]
        
        if (showMilestoneGlow && !isMilestoneClaimAnim) {
            val glowTex = Identifier.of("dailyrewards", "textures/gui/glow_legendary2.png")
            val t = (System.currentTimeMillis() % 2000L).toFloat() / 2000f
            val scaleVariation = 1f + 0.1f * kotlin.math.sin(t * 2f * Math.PI).toFloat()
            val rotationAngle = t * 360f
            
            val glowSize = (radius * 5f).toInt()
            val glowDrawX = mCenterX - glowSize / 2
            val glowDrawY = centerY.toInt() - glowSize / 2
            
            context.matrices.push()
            context.matrices.translate(mCenterX.toFloat(), centerY.toFloat(), 0f)
            context.matrices.scale(scaleVariation, scaleVariation, 1f)
            context.matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(rotationAngle))
            context.matrices.translate(-mCenterX.toFloat(), -centerY.toFloat(), 0f)
            context.drawTexture(
                RenderLayer::getGuiTextured,
                glowTex,
                glowDrawX,
                glowDrawY,
                0f,
                0f,
                glowSize,
                glowSize,
                glowSize,
                glowSize
            )
            context.matrices.pop()
        }

        val milestoneStatus = when {
            actualBarStep >= 9 -> 2
            barPosition == 8 -> 1
            nextIndex == 9 -> 1
            showMilestoneGlow -> 2
            else -> 0
        }
        
        drawMilestoneGem(context, mCenterX.toFloat(), centerY.toFloat(), radius.toFloat(), milestoneFill, milestoneStatus)
        
    }

    private fun drawConnector(context: DrawContext, x1: Int, x2: Int, cy: Int, thickness: Int, color: Int) {
        if (x2 <= x1) return
        val half = thickness / 2
        context.fill(x1, cy - half, x2, cy - half + thickness, color)
    }

    private fun drawRoundedNode(context: DrawContext, cx: Float, cy: Float, radius: Float, fillColor: Int) {
        val borderColor = 0x33000000
        drawCircle(context, cx, cy, radius + 1.0f, borderColor)
        drawCircle(context, cx, cy, radius, fillColor)
    }
    
    private fun drawCircle(context: DrawContext, cxF: Float, cyF: Float, r: Float, color: Int) {
        drawSuperSampledCircle(context, cxF, cyF, r, color, 6)
    }
    
    private fun drawSuperSampledCircle(context: DrawContext, cxF: Float, cyF: Float, r: Float, color: Int, samples: Int) {
        val rgb = color and 0xFFFFFF
        val aBase = ((color ushr 24) and 0xFF).toFloat()
        val sampleStep = 1f / samples
        val halfSample = sampleStep * 0.5f
        
        val startY = (cyF - r - 1f).toInt()
        val endY = (cyF + r + 1f).toInt()
        
        for (y in startY..endY) {
            val startX = (cxF - r - 1f).toInt()
            val endX = (cxF + r + 1f).toInt()
            
            for (x in startX..endX) {
                var totalCoverage = 0f
                
                for (sy in 0 until samples) {
                    for (sx in 0 until samples) {
                        val testX = x + sx * sampleStep + halfSample
                        val testY = y + sy * sampleStep + halfSample
                        
                        val dx = testX - cxF
                        val dy = testY - cyF
                        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                        
                        val coverage = when {
                            distance <= r - 0.5f -> 1f
                            distance >= r + 0.5f -> 0f
                            else -> 1f - (distance - (r - 0.5f))
                        }
                        
                        totalCoverage += coverage.coerceIn(0f, 1f)
                    }
                }
                
                totalCoverage /= (samples * samples)
                
                if (totalCoverage > 0.005f) {
                    val alpha = (aBase * totalCoverage).toInt().coerceIn(1, 255)
                    val finalColor = (alpha shl 24) or rgb
                    context.fill(x, y, x + 1, y + 1, finalColor)
                }
            }
        }
    }

    private fun drawGradientConnector(
        context: DrawContext,
        x1: Int,
        x2: Int,
        cy: Int,
        thickness: Int,
        colorStart: Int,
        colorEnd: Int
    ) {
        if (x2 <= x1) return
        val len = x2 - x1
        val half = thickness / 2
        for (i in 0 until len) {
            val t = i.toFloat() / (len - 1).coerceAtLeast(1)
            val col = lerpColor(colorStart, colorEnd, t)
            val x = x1 + i
            context.fill(x, cy - half, x + 1, cy - half + thickness, col)
        }
    }

    private fun lerpColor(a: Int, b: Int, t: Float): Int {
        val ta = ((a ushr 24) and 0xFF)
        val tr = ((a ushr 16) and 0xFF)
        val tg = ((a ushr 8) and 0xFF)
        val tb = (a and 0xFF)
        val ua = ((b ushr 24) and 0xFF)
        val ur = ((b ushr 16) and 0xFF)
        val ug = ((b ushr 8) and 0xFF)
        val ub = (b and 0xFF)
        val ra = (ta + ((ua - ta) * t)).toInt().coerceIn(0, 255)
        val rr = (tr + ((ur - tr) * t)).toInt().coerceIn(0, 255)
        val rg = (tg + ((ug - tg) * t)).toInt().coerceIn(0, 255)
        val rb = (tb + ((ub - tb) * t)).toInt().coerceIn(0, 255)
        return (ra shl 24) or (rr shl 16) or (rg shl 8) or rb
    }


    private fun drawLiquidConnector(
        context: DrawContext,
        x1: Int,
        x2: Int,
        cy: Int,
        thickness: Int,
        t: Float,
        colorStart: Int,
        colorEnd: Int,
        colorBg: Int
    ) {
        if (x2 <= x1) return
        val half = thickness / 2
        val len = x2 - x1
        val pos = (x1 + len * t).toInt()
        val ramp = (len * 0.25f).toInt().coerceIn(6, 18)
        context.fill(x1, cy - half, x2, cy - half + thickness, colorBg)
        val leftSolidEnd = (pos - ramp).coerceAtLeast(x1)
        if (leftSolidEnd > x1) {
            context.fill(x1, cy - half, leftSolidEnd, cy - half + thickness, colorStart)
        }
        val rampStart = (pos - ramp).coerceAtLeast(x1)
        val rampEnd = pos.coerceAtMost(x2)
        val rampLen = (rampEnd - rampStart).coerceAtLeast(0)
        if (rampLen > 0) {
            for (i in 0 until rampLen) {
                val tt = i.toFloat() / (rampLen - 1).coerceAtLeast(1)
                val col = lerpColor(colorStart, colorEnd, tt)
                val x = rampStart + i
                context.fill(x, cy - half, x + 1, cy - half + thickness, col)
            }
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
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun shouldPause(): Boolean = false

    private fun drawDropShadow(context: DrawContext, cx: Float, cy: Float, radius: Float) {
        val shadowOffset = 2f
        val shadowRadius = radius + 1f
        val shadowColor = 0x40000000
        drawRoundedNode(context, cx + shadowOffset, cy + shadowOffset, shadowRadius, shadowColor)
    }
    
    private fun drawAnimatedBorder(context: DrawContext, cx: Float, cy: Float, radius: Float) {
        val t = (System.currentTimeMillis() % 2000L).toFloat() / 2000f
        val borderRadius = radius + 2f + kotlin.math.sin(t * 2f * Math.PI).toFloat()
        val borderColor = 0x80FFD700.toInt()
        drawRoundedNode(context, cx, cy, borderRadius, borderColor)
    }
    
    private fun drawEnhancedNode(context: DrawContext, cx: Float, cy: Float, radius: Float, fillColor: Int, status: Int) {
        val centerColor = when (status) {
            2 -> lightenColor(fillColor, 0.3f)
            1 -> lightenColor(fillColor, 0.4f)
            else -> lightenColor(fillColor, 0.2f)
        }
        
        val layers = 8
        for (i in layers downTo 1) {
            val layerRadius = radius * (i.toFloat() / layers)
            val t = 1f - (i.toFloat() / layers)
            val layerColor = lerpColor(centerColor, fillColor, t)
            drawRoundedNode(context, cx, cy, layerRadius, layerColor)
        }
        
        val borderColor = darkenColor(fillColor, 0.3f)
        drawRoundedNode(context, cx, cy, radius + 0.5f, borderColor)
        drawRoundedNode(context, cx, cy, radius - 0.5f, fillColor)
    }
    
    private fun drawMilestoneGem(context: DrawContext, cx: Float, cy: Float, radius: Float, fillColor: Int, status: Int = 0) {
        val pulseTime = (System.currentTimeMillis() % 1500L).toFloat() / 1500f
        val pulseScale = 1f + 0.15f * kotlin.math.sin(pulseTime * 2f * Math.PI).toFloat()
        
        val animatedRadius = radius
        
        val facets = 6
        val angleStep = (2f * Math.PI / facets).toFloat()
        
        val baseColor = when (status) {
            2 -> 0xFF4CAF50.toInt()
            1 -> fillColor
            else -> 0xFF606060.toInt()
        }
        
        val lightColor = lightenColor(baseColor, 0.5f)
        val darkColor = darkenColor(baseColor, 0.3f)
        
        for (i in 0 until facets) {
            val angle = i * angleStep
            val nextAngle = (i + 1) * angleStep
            
            val x1 = cx + kotlin.math.cos(angle) * animatedRadius * 0.8f
            val y1 = cy + kotlin.math.sin(angle) * animatedRadius * 0.8f
            val x2 = cx + kotlin.math.cos(nextAngle) * animatedRadius * 0.8f
            val y2 = cy + kotlin.math.sin(nextAngle) * animatedRadius * 0.8f
            
            val facetColor = if (i % 2 == 0) lightColor else darkColor
            
            val backgroundRadius = animatedRadius * 0.4f * pulseScale
            val backgroundColor = darkenColor(facetColor, 0.3f)
            drawEnhancedNode(context, x1, y1, backgroundRadius, backgroundColor, status)
            
            val facetRadius = animatedRadius * 0.24f * pulseScale
            drawEnhancedNode(context, x1, y1, facetRadius, facetColor, status)
        }
        
        val centerRadius = animatedRadius * 0.4f * pulseScale
        drawEnhancedNode(context, cx, cy, centerRadius, lightenColor(baseColor, 0.6f), status)
    }
    
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
            val endY = 430
            val textY = startY + (endY - startY) * smoothText
            
            val textAlpha = (255f * textProgress).toInt()
            val textColor = 0x00FFAA00 or (textAlpha shl 24)
            
            context.drawTextWithShadow(textRenderer, text, textX, textY.toInt(), textColor)
        }
        
        if (animProgress >= 1f) {
            isMilestoneClaimAnim = false
        }
    }
    
    private fun smoothstep(t: Float): Float {
        return t * t * (3f - 2f * t)
    }

    private fun drawEnhanced3DConnector(context: DrawContext, x1: Int, x2: Int, cy: Int, thickness: Int, colorStart: Int, colorEnd: Int) {
        if (x2 <= x1) return
        
        val len = x2 - x1
        val half = thickness / 2
        
        for (y in -half until half) {
            val verticalT = (y + half).toFloat() / thickness.toFloat()
            
            val tubeShading = when {
                verticalT < 0.3f -> 0.7f + 0.3f * (verticalT / 0.3f)
                verticalT > 0.7f -> 0.7f + 0.3f * ((1f - verticalT) / 0.3f)
                else -> 1f
            }
            
            for (i in 0 until len) {
                val horizontalT = i.toFloat() / (len - 1).coerceAtLeast(1)
                val baseColor = lerpColor(colorStart, colorEnd, horizontalT)
                val shadedColor = multiplyColorBrightness(baseColor, tubeShading)
                
                val x = x1 + i
                context.fill(x, cy + y, x + 1, cy + y + 1, shadedColor)
            }
        }
        
        val highlightY = cy - half + 1
        for (i in 0 until len) {
            val horizontalT = i.toFloat() / (len - 1).coerceAtLeast(1)
            val baseColor = lerpColor(colorStart, colorEnd, horizontalT)
            val highlightColor = lightenColor(baseColor, 0.4f)
            context.fill(x1 + i, highlightY, x1 + i + 1, highlightY + 1, highlightColor)
        }
    }
    
    private fun lightenColor(color: Int, factor: Float): Int {
        val a = (color ushr 24) and 0xFF
        val r = ((color ushr 16) and 0xFF)
        val g = ((color ushr 8) and 0xFF)
        val b = (color and 0xFF)
        
        val newR = (r + (255 - r) * factor).toInt().coerceIn(0, 255)
        val newG = (g + (255 - g) * factor).toInt().coerceIn(0, 255)
        val newB = (b + (255 - b) * factor).toInt().coerceIn(0, 255)
        
        return (a shl 24) or (newR shl 16) or (newG shl 8) or newB
    }
    
    private fun darkenColor(color: Int, factor: Float): Int {
        val a = (color ushr 24) and 0xFF
        val r = ((color ushr 16) and 0xFF)
        val g = ((color ushr 8) and 0xFF)
        val b = (color and 0xFF)
        
        val newR = (r * (1f - factor)).toInt().coerceIn(0, 255)
        val newG = (g * (1f - factor)).toInt().coerceIn(0, 255)
        val newB = (b * (1f - factor)).toInt().coerceIn(0, 255)
        
        return (a shl 24) or (newR shl 16) or (newG shl 8) or newB
    }
    
    private fun multiplyColorBrightness(color: Int, brightness: Float): Int {
        val a = (color ushr 24) and 0xFF
        val r = ((color ushr 16) and 0xFF)
        val g = ((color ushr 8) and 0xFF)
        val b = (color and 0xFF)
        
        val newR = (r * brightness).toInt().coerceIn(0, 255)
        val newG = (g * brightness).toInt().coerceIn(0, 255)
        val newB = (b * brightness).toInt().coerceIn(0, 255)
        
        return (a shl 24) or (newR shl 16) or (newG shl 8) or newB
    }
}
