package foundation.esoteric.fireworkwarslobby.listeners

import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.profiles.Rank
import foundation.esoteric.fireworkwarscore.util.FireworkCreator
import foundation.esoteric.fireworkwarscore.util.sendMessage
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.meta.FireworkMeta

class PlayerConnectionListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val config = plugin.configManager.lobbyConfig
    private val coreConfig = plugin.core.pluginConfig

    private val lobbyWorld = config.getWorld()

    private val scoreboardManager = plugin.lobbyScoreboardManager

    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (player.world.uid == lobbyWorld.uid) {
            handlePlayerJoinLobby(player)
        }

        val ip = coreConfig.serverIp
        val invite = coreConfig.discordInvite

        player.sendPlayerListHeaderAndFooter(
            plugin.languageManager.getMessage(Message.TABLIST_HEADER, player, *emptyArray()),
            plugin.languageManager.getMessage(Message.TABLIST_FOOTER, player, ip, invite))

        plugin.core.friendManager.getReceivingRequests(player).forEach {
            val profile = plugin.playerDataManager.getPlayerProfile(it)
            player.sendMessage(Message.FRIEND_REQUEST_FROM, profile.formattedName())
        }

        event.joinMessage(null)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        scoreboardManager.deleteScoreboard(event.player)
        scoreboardManager.refreshAllScoreboards()

        event.quitMessage(null)
    }

    fun handlePlayerJoinLobby(player: Player) {
        player.teleport(config.spawnLocation.toBukkit())

        val profile = plugin.playerDataManager.getPlayerProfile(player)
        profile.username = player.name

        lobbyWorld.players.forEach {
            if (profile.rank == Rank.NONE) {
                it.sendMessage(Message.PLAYER_JOINED_LOBBY, profile.formattedName())
            } else {
                it.sendMessage(Message.RANKED_PLAYER_JOINED_LOBBY, profile.formattedName())
            }
        }

        if (profile.rank == Rank.GOLD) {
            config.randomFireworkLocations().forEach {
                lobbyWorld.spawn(it, Firework::class.java) { fw ->
                    val randomFirework = FireworkCreator.randomSupplyDropFirework()
                    fw.fireworkMeta = randomFirework.itemMeta as FireworkMeta

                    fw.setNoPhysics(true)
                    fw.ticksToDetonate = (20..30).random()
                }
            }
        }

        if (profile.firstJoin) {
            profile.firstJoin = false
            player.sendMessage(Message.WELCOME, profile.formattedName())
        }

        profile.updateOwnTablist()

        plugin.npcManager.npcList.forEach { it.sendInitPackets(player) }

        scoreboardManager.createOrUpdateScoreboard(player)
        scoreboardManager.refreshAllScoreboards()
    }
}