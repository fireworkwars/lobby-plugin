package foundation.esoteric.fireworkwarslobby.listeners

import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerWorldChangeListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerWorldChange(event: PlayerChangedWorldEvent) {
        if (event.player.world != plugin.configManager.lobbyConfig.getWorld()) {
            return
        }

        plugin.logLoudly("player joined lobby world!", true)

        plugin.npcManager.npcList.forEach { it.sendInitPackets(event.player) }
    }
}