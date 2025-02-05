package xyz.fireworkwars.lobby.listeners

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import xyz.fireworkwars.core.interfaces.Event
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin

class PlayerChatListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val lobbyWorld = plugin.configManager.lobbyConfig.getWorld()

    override fun register() {
        plugin.registerEvent(this)
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerChat(event: AsyncChatEvent) {
        event.renderer(ChatRenderer.viewerUnaware { _, _, message -> message })

        event.viewers().removeIf { it is Player }
        event.viewers().addAll(lobbyWorld.players)

        event.message(this.formatMessage(event.player, event.originalMessage()))
    }

    private fun formatMessage(player: Player, message: Component): Component {
        val profile = plugin.playerDataManager.getPlayerProfile(player)
        return profile.formattedName().append(Component.text(": ").append(message).color(NamedTextColor.WHITE))
    }
}