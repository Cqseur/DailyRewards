package cqseur.dailyrewards.utils

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

/**
 * Utility class 
 **/
object MessageUtils {

    /**
     * Creates a text with dynamic gradient
     * @param text The text to color
     * @param colors The gradient colors (minimum 2 0xHEX)
     * @return MutableComponent with applied gradient
     **/
    fun gradientText(text: String, vararg colors: Int): MutableComponent {
        require(colors.size >= 2) { "Au moins 2 couleurs sont nécessaires pour un dégradé" }
        
        val root = Component.empty()
        fun lerp(a: Int, b: Int, t: Float) = (a + ((b - a) * t)).toInt()
        
        for (i in text.indices) {
            val progress = if (text.length == 1) 0.5f else i.toFloat() / (text.length - 1)
            
            val segmentCount = colors.size - 1
            val segmentSize = 1f / segmentCount
            val segmentIndex = (progress / segmentSize).toInt().coerceIn(0, segmentCount - 1)
            val localProgress = (progress - (segmentIndex * segmentSize)) / segmentSize
            
            val startColor = colors[segmentIndex]
            val endColor = colors[segmentIndex + 1]
            
            val r = lerp(startColor shr 16 and 0xFF, endColor shr 16 and 0xFF, localProgress)
            val g = lerp(startColor shr 8 and 0xFF, endColor shr 8 and 0xFF, localProgress)
            val b = lerp(startColor and 0xFF, endColor and 0xFF, localProgress)
            val rgb = (r shl 16) or (g shl 8) or b
            
            root.append(Component.literal(text[i].toString()).withColor(rgb))
        }
        return root
    }
    
    private fun buildGradientPrefixText(): MutableComponent {
        val start = 0xF2C511 /* #F2C511 */
        val color2 = 0xF39C19 /* #F39C19 */
        val color3 = 0xFF0000 /* #FF0000 */
        val color4 = 0xb300ff /* #b300ff */
        /* val end = 0x7541ea  #7541ea */
        val prefixname = "DailyRewards"
        val root = Component.empty().append(Component.literal("[").withStyle(ChatFormatting.GOLD))
        root.append(gradientText(prefixname, start, color2, color3, color4/* , end */))
        root.append(Component.literal("] ").withStyle(ChatFormatting.GOLD))
        return root
    }

    fun rainbowText(text: String): MutableComponent {
        return gradientText(text, 0xFF0000, 0xFF7F00, 0xFFFF00, 0x00FF00, 0x0000FF, 0x4B0082, 0x9400D3)
    }
    
    fun PREFIX(): MutableComponent {
        return buildGradientPrefixText()
    }
    

    fun sendMessage(message: String) {
        Minecraft.getInstance().player?.sendSystemMessage(
            PREFIX().append(Component.literal(message))
        )
    }

    fun sendInfo(message: String) {
        val player = Minecraft.getInstance().player
        val fullMessage = PREFIX().append(Component.literal(message))
        player?.sendSystemMessage(fullMessage)
    }
    
    fun sendSuccess(message: String) {
        val player = Minecraft.getInstance().player
        val fullMessage = PREFIX().append(Component.literal("§a$message"))
        player?.sendSystemMessage(fullMessage)
    }
    
    fun sendError(message: String) {
        val player = Minecraft.getInstance().player
        val fullMessage = PREFIX().append(Component.literal("§c$message"))
        player?.sendSystemMessage(fullMessage)
    }
    
    fun sendWarning(message: String) {
        val player = Minecraft.getInstance().player
        val fullMessage = PREFIX().append(Component.literal("§e$message"))
        player?.sendSystemMessage(fullMessage)
    }
}
