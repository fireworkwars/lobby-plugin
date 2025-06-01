package xyz.fireworkwars.lobby.listeners

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import org.bukkit.event.EventHandler
import xyz.fireworkwars.core.interfaces.Event
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import kotlin.math.roundToInt
import kotlin.math.sin

class ServerListPingListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    override fun register() {
        plugin.registerEvent(this)
    }

    @EventHandler
    fun onServerListPing(event: PaperServerListPingEvent) {
        event.numPlayers = 8 + sin(0.005 * plugin.server.currentTick).roundToInt() + plugin.server.onlinePlayers.size
    }
}