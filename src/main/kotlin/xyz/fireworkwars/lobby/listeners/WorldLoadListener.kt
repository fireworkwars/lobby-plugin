package xyz.fireworkwars.lobby.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.world.WorldLoadEvent
import xyz.fireworkwars.core.interfaces.Event
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import xyz.fireworkwars.lobby.firework_show.FireworkShowRunnable

class WorldLoadListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val lobbyWorld = plugin.configManager.lobbyConfig.getWorld()

    override fun register() {
        plugin.registerEvent(this)
    }

    @EventHandler
    fun onLobbyLoad(event: WorldLoadEvent) {
        if (event.world.uid != lobbyWorld.uid) {
            return
        }

        FireworkShowRunnable(plugin).start()
        unregister()
    }
}