package xyz.fireworkwars.lobby.listeners

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import org.bukkit.event.EventHandler
import xyz.fireworkwars.core.interfaces.Event
import xyz.fireworkwars.core.language.Message
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import kotlin.math.roundToInt
import kotlin.math.sin

class ServerListPingListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val config = plugin.configManager.lobbyConfig
    private val server = plugin.server

    override fun register() {
        plugin.registerEvent(this)
    }

    @EventHandler
    fun onServerListPing(event: PaperServerListPingEvent) {
        event.numPlayers = config.fakePlayers + sin(0.001 * server.currentTick).roundToInt().coerceAtLeast(0) + server.onlinePlayers.size
        event.motd(plugin.languageManager.getDefaultMessage(Message.valueOf(config.motdMessages.random())))
    }
}