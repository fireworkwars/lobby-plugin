package foundation.esoteric.fireworkwarslobby.listeners

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.event.EventHandler
import org.bukkit.inventory.EquipmentSlot

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
    }
}