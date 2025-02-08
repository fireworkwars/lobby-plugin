package xyz.fireworkwars.lobby.leaderboard

import org.bukkit.entity.Player
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import java.util.*

class LeaderboardManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val config = plugin.configManager.lobbyConfig.leaderboards

    private val leaderboards = mutableMapOf<UUID, List<LeaderboardDisplay>>()
    val leaderboardMap = mutableMapOf<Int, LeaderboardDisplay>()

    init {
        plugin.runTaskTimer(0L, 20L * 30) {
            this.updateAll()
        }
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