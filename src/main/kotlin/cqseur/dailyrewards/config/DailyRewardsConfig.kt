package cqseur.dailyrewards.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path

object DailyRewardsConfig {
    const val PREFIX: String = "§6[§eDailyRewards§6]§r "

    private val gson = Gson()
    private val configPath: Path = FabricLoader.getInstance().configDir.resolve("dailyrewards.json")

    var flipAnimation: Boolean = true
    var flipSpeed: Float = 0.5f
    var debugMode: Boolean = false
    var showOverlay: Boolean = true
    var overlayOpacityPercent: Int = 67
        public set

    init {
        load()
    }

    private fun load() {
        if (Files.exists(configPath)) {
            try {
                Files.newBufferedReader(configPath).use { reader ->
                    val obj = gson.fromJson(reader, JsonObject::class.java)
                    if (obj.has("flipAnimation")) flipAnimation = obj.get("flipAnimation").asBoolean
                    if (obj.has("flipSpeed")) flipSpeed = obj.get("flipSpeed").asFloat
                    if (obj.has("debugMode")) debugMode = obj.get("debugMode").asBoolean
                    if (obj.has("showOverlay")) showOverlay = obj.get("showOverlay").asBoolean
                    if (obj.has("overlayOpacity")) overlayOpacityPercent = obj.get("overlayOpacity").asInt
                }
            } catch (e: Exception) {
            }
        } else {
            save()
        }
    }

    fun save() {
        val obj = JsonObject()
        obj.addProperty("flipAnimation", flipAnimation)
        obj.addProperty("flipSpeed", flipSpeed)
        obj.addProperty("debugMode", debugMode)
        obj.addProperty("showOverlay", showOverlay)
        obj.addProperty("overlayOpacity", overlayOpacityPercent)
        Files.createDirectories(configPath.parent)
        Files.newBufferedWriter(configPath).use { writer ->
            gson.toJson(obj, writer)
        }
    }
}
