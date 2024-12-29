package foundation.esoteric.fireworkwarslobby.listeners

import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.util.sendMessage
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val lobbyWorld = plugin.configManager.lobbyConfig.getWorld()

    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (player.world.uid != lobbyWorld.uid) {
            return
        }

        val profile = plugin.playerDataManager.getPlayerProfile(player, true)!!

        lobbyWorld.players.forEach {
            if (profile.ranked) {
                it.sendMessage(Message.RANKED_PLAYER_JOINED_LOBBY, it.name())
            } else {
                it.sendMessage(Message.PLAYER_JOINED_LOBBY, it.name())
            }
        }

        if (profile.firstJoin) {
            profile.firstJoin = false
            player.sendMessage(Message.WELCOME, player.name())
        }

        plugin.npcManager.npcList.forEach { it.sendInitPackets(player) }

        event.joinMessage(null)
    }
}