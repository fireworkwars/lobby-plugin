package foundation.esoteric.fireworkwarslobby.scoreboards

import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.libs.fastboard.adventure.FastBoard
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*

class LobbyScoreboardManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val languageManager = plugin.languageManager
    private val scoreboards = mutableMapOf<UUID, FastBoard>()

    fun createOrUpdateScoreboard(player: Player) {
        if (scoreboards.containsKey(player.uniqueId)) {
            updateScoreboard(player)
        } else {
            createScoreboard(player)
        }
    }

    private fun createScoreboard(player: Player) {
        val version = plugin.core.fireworkWarsPluginData.getVersionString()

        val board = FastBoard(player).apply {
            updateTitle(languageManager.getMessage(Message.LOBBY_SB_TITLE, player, version))
        }

        scoreboards[player.uniqueId] = board
    }

    fun refreshAllScoreboards() {
        scoreboards.keys.forEach { updateScoreboard(plugin.server.getPlayer(it)!!) }
    }

    private fun updateScoreboard(player: Player) {
        val board = scoreboards[player.uniqueId] ?: return
        val profile = plugin.playerDataManager.getPlayerProfile(player)

        board.updateLines(mutableListOf(
            languageManager.getMessage(Message.SB_SEPARATOR, player, *emptyArray()),
            languageManager.getMessage(Message.LOBBY_SB_RANK, player, profile.rank.toFormattedText()),
            languageManager.getMessage(Message.LOBBY_SB_ACHIEVEMENTS, player, 0, 0),
            Component.empty(),
            languageManager.getMessage(Message.LOBBY_SB_PLAYERS, player, plugin.server.onlinePlayers.size),
            languageManager.getMessage(Message.LOBBY_SB_FRIENDS, player, 0),
            Component.empty(),
            languageManager.getMessage(Message.LOBBY_SB_IP, player, plugin.core.pluginConfig.serverIp),
            languageManager.getMessage(Message.SB_SEPARATOR, player, *emptyArray())
        ))
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