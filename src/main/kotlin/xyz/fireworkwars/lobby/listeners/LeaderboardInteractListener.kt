package xyz.fireworkwars.lobby.listeners

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.inventory.EquipmentSlot
import xyz.fireworkwars.core.interfaces.Event
import xyz.fireworkwars.core.util.playSound
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin

class LeaderboardInteractListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    override fun register() {
        plugin.registerEvent(this)
    }

    @EventHandler
    fun onLeaderboardInteract(event: PlayerUseUnknownEntityEvent) {
        if (event.hand != EquipmentSlot.HAND) {
            return
        }

        if (event.clickedRelativePosition != null) {
            return
        }

        val id = event.entityId
        val leaderboard = plugin.leaderboardManager.leaderboardMap[id] ?: return

        leaderboard.timePeriod = leaderboard.timePeriod.next()
        leaderboard.updateAndSendPackets()

        event.player.playSound(Sound.BLOCK_LEVER_CLICK)
    }
}