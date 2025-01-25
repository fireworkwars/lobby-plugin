package foundation.esoteric.fireworkwarslobby.leaderboard

import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.entity.Player
import java.util.*

class LeaderboardManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val config = plugin.configManager.lobbyConfig

    private val killLeaderboards = mutableMapOf<UUID, LeaderboardDisplay>()
    private val winLeaderboards = mutableMapOf<UUID, LeaderboardDisplay>()

    init {
        plugin.runTaskTimer({
            this.updateAll()
        }, 0L, 20L * 10)
    }

    fun createOrUpdateLeaderboard(player: Player) {
        killLeaderboards.computeIfAbsent(player.uniqueId) {
            LeaderboardDisplay(config.leaderboards.allTimeKillsLocation)
        }.let { this.updateLeaderboard(player, it) }

        winLeaderboards.computeIfAbsent(player.uniqueId) {
            LeaderboardDisplay(config.leaderboards.allTimeWinsLocation)
        }.let { this.updateLeaderboard(player, it) }
    }

    private fun updateLeaderboard(player: Player, leaderboard: LeaderboardDisplay) {
        leaderboard.updateAndSendPackets(player)
    }

    private fun updateAll() {
        killLeaderboards.forEach { (uuid, leaderboard) ->
            plugin.server.getPlayer(uuid)?.let { this.updateLeaderboard(it, leaderboard) }
        }

        winLeaderboards.forEach { (uuid, leaderboard) ->
            plugin.server.getPlayer(uuid)?.let { this.updateLeaderboard(it, leaderboard) }
        }
    }

    fun deleteLeaderboard(player: Player) {
        killLeaderboards.remove(player.uniqueId)
        winLeaderboards.remove(player.uniqueId)
    }
}