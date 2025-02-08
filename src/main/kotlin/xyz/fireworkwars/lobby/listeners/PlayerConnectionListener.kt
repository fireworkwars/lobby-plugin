package xyz.fireworkwars.lobby.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.fireworkwars.core.interfaces.Event
import xyz.fireworkwars.core.language.Message
import xyz.fireworkwars.core.profiles.Rank
import xyz.fireworkwars.core.util.FireworkCreator
import xyz.fireworkwars.core.util.prepareAndTeleport
import xyz.fireworkwars.core.util.sendMessage
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin

class PlayerConnectionListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val config = plugin.configManager.lobbyConfig
    private val coreConfig = plugin.core.pluginConfig

    private val lobbyWorld = config.getWorld()

    private val scoreboardManager = plugin.lobbyScoreboardManager

    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val profile = plugin.playerDataManager.getPlayerProfile(player, true)!!

        if (player.world.uid == lobbyWorld.uid) {
            this.handlePlayerJoinLobby(player)
        }

        val ip = coreConfig.serverIp
        val invite = coreConfig.discordInvite

        player.sendPlayerListHeaderAndFooter(
            plugin.languageManager.getMessage(Message.TABLIST_HEADER, player, *emptyArray()),
            plugin.languageManager.getMessage(Message.TABLIST_FOOTER, player, ip, invite)
        )

        plugin.core.friendManager.getReceivingRequestUUIDs(player).forEach {
            val otherProfile = plugin.playerDataManager.getPlayerProfile(it)
            player.sendMessage(Message.FRIEND_REQUEST_FROM, otherProfile.formattedName(), otherProfile.username)
        }

        profile.friends.forEach {
            val offlinePlayer = plugin.server.getOfflinePlayer(it)

            if (offlinePlayer.isOnline) {
                offlinePlayer.player!!.sendMessage(Message.FRIEND_JOINED, profile.formattedName())
            }
        }

        event.joinMessage(null)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        plugin.leaderboardManager.deleteLeaderboard(event.player)
        scoreboardManager.deleteScoreboard(event.player)

        plugin.runTaskOneTickLater { scoreboardManager.refreshAllScoreboards() }

        val profile = plugin.playerDataManager.getPlayerProfile(event.player)

        profile.lastSeenDate = System.currentTimeMillis()

        profile.friends.forEach {
            val offlinePlayer = plugin.server.getOfflinePlayer(it)

            if (offlinePlayer.isOnline) {
                offlinePlayer.player!!.sendMessage(Message.FRIEND_LEFT, profile.formattedName())
            }
        }

        event.quitMessage(null)
    }

    fun handlePlayerJoinLobby(player: Player) {
        player.prepareAndTeleport(config.spawnLocation.toBukkit())

        val profile = plugin.playerDataManager.getPlayerProfile(player)

        profile.username = player.name
        profile.lastSeenDate = System.currentTimeMillis()

        lobbyWorld.players.forEach {
            if (profile.rank == Rank.NONE) {
                it.sendMessage(Message.PLAYER_JOINED_LOBBY, profile.formattedName())
            } else {
                it.sendMessage(Message.RANKED_PLAYER_JOINED_LOBBY, profile.formattedName())
            }
        }

        if (profile.rank == Rank.GOLD) {
            config.randomFireworkLocations().forEach {
                FireworkCreator.sendSupplyDropFirework(it, (20..30).random())
            }
        }

        if (profile.firstJoin) {
            profile.firstJoin = false
            profile.firstJoinDate = System.currentTimeMillis()

            player.sendMessage(Message.WELCOME, profile.formattedName())
        }

        profile.updateOwnTablist()

        plugin.runTaskOneTickLater { plugin.core.fireworkWarsHook.resyncPlayerVisibility() }

        plugin.npcManager.npcList.forEach { it.sendInitPackets(player) }
        plugin.leaderboardManager.createOrUpdateLeaderboard(player)

        scoreboardManager.createOrUpdateScoreboard(player)
        scoreboardManager.refreshAllScoreboards()
    }
}