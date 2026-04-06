package cqseur.dailyrewards.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path

/**
 * Centralized configuration manager for the mod
 * Handles loading, saving, and managing configuration data
 **/
object ConfigManager {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configPath: Path = FabricLoader.getInstance().configDir.resolve("dailyrewards.json")

    var config: ConfigData = ConfigData()
        private set

    fun init() {
        loadConfig()
    }
    
    fun loadConfig() {
        if (Files.exists(configPath)) {
            try {
                Files.newBufferedReader(configPath).use { reader ->
                    val loadedConfig = gson.fromJson(reader, ConfigData::class.java)
                    if (loadedConfig != null) {
                        config = loadedConfig
                    }
                }
            } catch (e: Exception) {
                println("Error loading DailyRewards config: ${e.message}")
                saveConfig()
            }
        } else {
            saveConfig()
        }
    }
    
    fun saveConfig() {
        try {
            Files.createDirectories(configPath.parent)
            Files.newBufferedWriter(configPath).use { writer ->
                gson.toJson(config, writer)
            }
        } catch (e: Exception) {
            println("Error saving DailyRewards config: ${e.message}")
        }
    }
}
