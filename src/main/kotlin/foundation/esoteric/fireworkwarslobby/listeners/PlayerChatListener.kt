package foundation.esoteric.fireworkwarslobby.listeners

import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

class PlayerChatListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val lobbyWorld = plugin.configManager.lobbyConfig.getWorld()

    override fun register() {
        plugin.registerEvent(this)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        if (event.player.world.uid != lobbyWorld.uid) {
            return
        }

        event.renderer(ChatRenderer.viewerUnaware { _, _, message -> message })
        event.viewers().removeIf(lobbyWorld.players::contains)
        event.message(this.formatMessage(event.player, event.originalMessage()))
    }

    private fun formatMessage(player: Player, message: Component): Component {
        val profile = plugin.playerDataManager.getPlayerProfile(player)
        return profile.formattedName().append(Component.text(": ").color(NamedTextColor.WHITE)).append(message)
    }
}