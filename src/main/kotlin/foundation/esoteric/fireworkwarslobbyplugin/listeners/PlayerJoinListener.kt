package foundation.esoteric.fireworkwarslobbyplugin.listeners

import foundation.esoteric.fireworkwarslobbyplugin.FireworkWarsLobbyPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val plugin: FireworkWarsLobbyPlugin) : Listener {
    private val lobbyWorld = plugin.configManager.lobbyConfig.getWorld()

    fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (player.world.uid != lobbyWorld.uid) {
            return
        }

        //todo: language manager pls

        lobbyWorld.players.forEach {
            it.sendMessage("${player.name} has joined the lobby!")

            if (it == player) {
                it.sendMessage("Welcome to Firework Wars!")
            }
        }
    }
}