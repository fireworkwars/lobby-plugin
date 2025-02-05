package xyz.fireworkwars.lobby.communication

import foundation.esoteric.fireworkwarscore.communication.LobbyPluginData
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin

class LobbyPluginDataHolder(private val plugin: FireworkWarsLobbyPlugin) : LobbyPluginData {
    private val config = plugin.configManager.lobbyConfig

    override fun getLobbySpawn(): Location {
        return config.spawnLocation.toBukkit()
    }

    override fun isLobby(world: World): Boolean {
        return world == config.getWorld()
    }

    override fun updateScoreboards() {
        plugin.lobbyScoreboardManager.refreshAllScoreboards()
    }

    override fun setTagVisibility(player: Player, visible: Boolean) {
        plugin.nameTagManager.setNameTagVisible(player, visible)
    }
}