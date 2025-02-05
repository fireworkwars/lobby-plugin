package xyz.fireworkwars.lobby.config

import com.google.gson.GsonBuilder
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import xyz.fireworkwars.lobby.config.structure.LobbyData
import java.io.File
import java.io.FileReader
import java.io.IOException

class ConfigManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val configFile = "lobby.json"
    lateinit var lobbyConfig: LobbyData

    fun loadLobbyFromConfig() {
        plugin.saveResource(configFile, true)

        val arenasFilePath = plugin.dataFolder.path + File.separator + configFile
        val file = File(arenasFilePath)

        val gson = GsonBuilder().create()

        try {
            FileReader(file).use { reader ->
                this.lobbyConfig = gson.fromJson(reader, LobbyData::class.java)
            }
        } catch (exception: IOException) {
            plugin.logger.severe("Failed to load lobby.json file: " + exception.message)
        }
    }
}