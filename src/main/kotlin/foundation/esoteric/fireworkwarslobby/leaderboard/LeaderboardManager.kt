package foundation.esoteric.fireworkwarslobby.leaderboard

import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.util.getMessage
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.entity.Player
import java.util.*

class LeaderboardManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val config = plugin.configManager.lobbyConfig.leaderboards

    private val winLeaderboards = mutableMapOf<UUID, LeaderboardDisplay>()
    private val killLeaderboards = mutableMapOf<UUID, LeaderboardDisplay>()

    init {
        plugin.runTaskTimer({
            this.updateAll()
        }, 0L, 20L * 10)
    }

    fun createOrUpdateLeaderboard(player: Player) {
        winLeaderboards.computeIfAbsent(player.uniqueId) {
            LeaderboardDisplay(plugin, config.allTimeWinsLocation, player).apply {
                this.title = player.getMessage(Message.LEADERBOARD_TITLE)
                this.subtitle = player.getMessage(Message.LEADERBOARD_TYPE_WINS)
                this.valueFunction = { it.stats.wins }
            }
        }.let(LeaderboardDisplay::updateAndSendPackets)

        killLeaderboards.computeIfAbsent(player.uniqueId) {
            LeaderboardDisplay(plugin, config.allTimeKillsLocation, player).apply {
                this.title = player.getMessage(Message.LEADERBOARD_TITLE)
                this.subtitle = player.getMessage(Message.LEADERBOARD_TYPE_KILLS)
                this.valueFunction = { it.stats.kills }
            }
        }.let(LeaderboardDisplay::updateAndSendPackets)
    }

    private fun updateAll() {
        winLeaderboards.values.forEach(LeaderboardDisplay::updateAndSendPackets)
        killLeaderboards.values.forEach(LeaderboardDisplay::updateAndSendPackets)
    }

    fun deleteLeaderboard(player: Player) {
        winLeaderboards.remove(player.uniqueId)
        killLeaderboards.remove(player.uniqueId)
    }
}