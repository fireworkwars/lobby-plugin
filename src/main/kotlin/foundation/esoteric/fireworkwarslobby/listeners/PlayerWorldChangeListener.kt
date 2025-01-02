package foundation.esoteric.fireworkwarslobby.listeners

import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerWorldChangeListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val lobbyWorld = plugin.configManager.lobbyConfig.getWorld()

    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerWorldChange(event: PlayerChangedWorldEvent) {
        if (event.player.world.uid == lobbyWorld.uid) {
            plugin.playerConnectionListener.handlePlayerJoinLobby(event.player)
        } else {
            plugin.lobbyScoreboardManager.deleteScoreboard(event.player)
        }
    }
}