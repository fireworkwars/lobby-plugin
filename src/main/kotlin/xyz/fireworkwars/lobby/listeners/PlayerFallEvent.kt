package xyz.fireworkwars.lobby.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import xyz.fireworkwars.core.interfaces.Event
import xyz.fireworkwars.core.util.prepareAndTeleport
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin

class PlayerFallEvent(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val lobbyWorld = plugin.configManager.lobbyConfig.getWorld()
    private val spawn = plugin.configManager.lobbyConfig.spawnLocation.toBukkit()

    override fun register() {
        plugin.registerEvent(this)
    }

    @EventHandler
    fun onPlayerFall(event: PlayerMoveEvent) {
        if (event.player.world != lobbyWorld) {
            return
        }

        if (event.to.y < spawn.y - 50) {
            event.player.prepareAndTeleport(spawn)
        }
    }
}