package xyz.fireworkwars.lobby.scoreboards

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import xyz.fireworkwars.core.language.Message
import xyz.fireworkwars.core.libs.fastboard.adventure.FastBoard
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import java.util.*

class LobbyScoreboardManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val languageManager = plugin.languageManager
    private val scoreboards = mutableMapOf<UUID, FastBoard>()

    fun createOrUpdateScoreboard(player: Player) {
        if (scoreboards.containsKey(player.uniqueId)) {
            this.updateScoreboard(player)
        } else {
            this.createScoreboard(player)
        }
    }

    private fun createScoreboard(player: Player) {
        val version = plugin.core.fireworkWarsServiceProvider.getVersionString()

        val board = FastBoard(player).apply {
            this.updateTitle(languageManager.getMessage(Message.LOBBY_SB_TITLE, player, version))
        }

        this.scoreboards[player.uniqueId] = board
    }

    fun refreshAllScoreboards() {
        scoreboards.keys.forEach { this.updateScoreboard(plugin.server.getPlayer(it)!!) }
    }

    private fun updateScoreboard(player: Player) {
        val board = scoreboards[player.uniqueId] ?: return
        val profile = plugin.playerDataManager.getPlayerProfile(player)

        board.updateLines(
            mutableListOf(
                languageManager.getMessage(Message.SB_SEPARATOR, player),
                languageManager.getMessage(Message.LOBBY_SB_RANK, player, profile.rank.toFormattedText()),
                languageManager.getMessage(Message.LOBBY_SB_ACHIEVEMENTS, player, 0, 15),
                Component.empty(),
                languageManager.getMessage(Message.LOBBY_SB_PLAYERS, player, plugin.server.onlinePlayers.size),
                languageManager.getMessage(Message.LOBBY_SB_FRIENDS, player, profile.getOnlineFriends().size),
                Component.empty(),
                languageManager.getMessage(Message.LOBBY_SB_IP, player, plugin.core.pluginConfig.serverIp),
                languageManager.getMessage(Message.SB_SEPARATOR, player)
            )
        )
    }

    fun deleteScoreboard(player: Player) {
        val board = scoreboards[player.uniqueId] ?: return

        try {
            board.delete()
        } catch (e: Exception) {
            plugin.logger.warning("Failed to delete scoreboard for ${player.name}")
        }

        scoreboards.remove(player.uniqueId)
    }
}