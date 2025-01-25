package foundation.esoteric.fireworkwarslobby.leaderboard

import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.util.getMessage
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.entity.Player
import java.util.*

class LeaderboardManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val config = plugin.configManager.lobbyConfig.leaderboards

    private val allTimeKills = "All-Time Kills"
    private val allTimeWins = "All-Time Wins"

    private val killLeaderboards = mutableMapOf<UUID, LeaderboardDisplay>()
    private val winLeaderboards = mutableMapOf<UUID, LeaderboardDisplay>()

    init {
        plugin.runTaskTimer({
            this.updateAll()
        }, 0L, 20L * 10)
    }

    fun createOrUpdateLeaderboard(player: Player) {
        killLeaderboards.computeIfAbsent(player.uniqueId) {
            LeaderboardDisplay(plugin, config.allTimeKillsLocation, player).apply {
                this.setTitleText(player.getMessage(Message.LEADERBOARD_TITLE))
                this.setSubtitleText(player.getMessage(Message.LEADERBOARD_TYPE, allTimeKills))
            }
        }.let(LeaderboardDisplay::updateAndSendPackets)

        winLeaderboards.computeIfAbsent(player.uniqueId) {
            LeaderboardDisplay(plugin, config.allTimeWinsLocation, player).apply {
                this.setTitleText(player.getMessage(Message.LEADERBOARD_TITLE))
                this.setSubtitleText(player.getMessage(Message.LEADERBOARD_TYPE, allTimeWins))
            }
        }.let(LeaderboardDisplay::updateAndSendPackets)
    }

    private fun updateAll() {
        killLeaderboards.values.forEach(LeaderboardDisplay::updateAndSendPackets)
        winLeaderboards.values.forEach(LeaderboardDisplay::updateAndSendPackets)
    }

    fun deleteLeaderboard(player: Player) {
        killLeaderboards.remove(player.uniqueId)
        winLeaderboards.remove(player.uniqueId)
    }
}