package xyz.fireworkwars.lobby.listeners

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import foundation.esoteric.fireworkwarscore.interfaces.Event
import org.bukkit.event.EventHandler
import org.bukkit.inventory.EquipmentSlot
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import xyz.fireworkwars.lobby.npc.gui.ArenaGUI

class NPCInteractListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onNPCInteract(event: PlayerUseUnknownEntityEvent) {
        if (event.hand != EquipmentSlot.HAND) {
            return
        }

        if (event.clickedRelativePosition != null) {
            return
        }

        val player = event.player

        val npc = plugin.npcManager.getNPC(event.entityId) ?: return
        val menuData = npc.data.menu

        ArenaGUI(plugin, menuData, player).show()
    }
}