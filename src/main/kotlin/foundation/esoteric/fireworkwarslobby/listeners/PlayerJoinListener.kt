package foundation.esoteric.fireworkwarslobby.listeners

import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.util.FireworkCreator
import foundation.esoteric.fireworkwarscore.util.sendMessage
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.meta.FireworkMeta

class PlayerJoinListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val config = plugin.configManager.lobbyConfig
    private val lobbyWorld = config.getWorld()

    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        handlePlayerJoinLobby(event.player)

        event.joinMessage(null)
    }

    fun handlePlayerJoinLobby(player: Player) {
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

        if (profile.ranked) {
            config.randomFireworkLocations().forEach {
                lobbyWorld.spawn(it, Firework::class.java) { fw ->
                    val randomFirework = FireworkCreator.randomSupplyDropFirework()
                    fw.fireworkMeta = randomFirework.itemMeta as FireworkMeta

                    fw.setNoPhysics(true)
                    fw.ticksToDetonate = (12..18).random()
                }
            }
        }

        if (profile.firstJoin) {
            profile.firstJoin = false
            player.sendMessage(Message.WELCOME, player.name())
        }

        plugin.npcManager.npcList.forEach { it.sendInitPackets(player) }
    }
}