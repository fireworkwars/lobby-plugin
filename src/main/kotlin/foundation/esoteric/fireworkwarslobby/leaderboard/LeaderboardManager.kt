package foundation.esoteric.fireworkwarslobby.leaderboard

import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.entity.Player
import java.util.*

class LeaderboardManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val config = plugin.configManager.lobbyConfig.leaderboards

    private val leaderboards = mutableMapOf<UUID, List<LeaderboardDisplay>>()
    val leaderboardMap = mutableMapOf<Int, LeaderboardDisplay>()

    init {
        plugin.runTaskTimer({
            this.updateAll()
        }, 0L, 20L * 30)
    }

    fun createOrUpdateLeaderboard(player: Player) {
        leaderboards.computeIfAbsent(player.uniqueId) {
            config.map {
                LeaderboardDisplay(it, plugin, player).apply {
                    leaderboardMap[id] = this
                }
            }
        }.forEach(LeaderboardDisplay::updateAndSendPackets)
    }
    
    private fun updateAll() {
        leaderboards.values.flatten().forEach(LeaderboardDisplay::updateAndSendPackets)
    }

    fun deleteLeaderboard(player: Player) {
        leaderboards.remove(player.uniqueId)?.let {
            it.forEach { display ->
                leaderboardMap.remove(display.id)
            }
        }
    }
}