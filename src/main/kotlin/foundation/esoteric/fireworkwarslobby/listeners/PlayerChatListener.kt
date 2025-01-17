package foundation.esoteric.fireworkwarslobby.listeners

import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler

class PlayerChatListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    override fun register() {
        plugin.registerEvent(this)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        if (event.player.world.uid != plugin.configManager.lobbyConfig.getWorld().uid) {
            return
        }

        val profile = plugin.playerDataManager.getPlayerProfile(event.player)
        event.message(profile.formattedName().append(Component.text(":")).appendSpace().append(event.message()))
    }
}