package foundation.esoteric.fireworkwarslobby.listeners

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.Gui
import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.EquipmentSlot

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

        val fireworkWarsData = plugin.core.fireworkWarsPluginData

        val npc = plugin.npcManager.getNPC(event.entityId)
        val menuData = npc.data.menu

        val gui = Gui.gui()
            .title(plugin.miniMessage.deserialize(menuData.title))
            .rows(3)
            .create()

        gui.setOpenGuiAction {
            val player = it.player as Player
            player.playSound(player, Sound.ITEM_CROSSBOW_LOADING_START, 1.0F, 1.0F)
        }

        gui.setCloseGuiAction {
            val player = it.player as Player
            player.playSound(player, Sound.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F)
        }

        gui.setDefaultClickAction {
            it.isCancelled = true

            val player = it.whoClicked as Player
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F, 1.0F)
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
        }

        val arenas = when (menuData.mapType) {
            "barracks" -> fireworkWarsData.getBarracksArenas()
            "town" -> fireworkWarsData.getTownArenas()
            else -> emptyList()
        }

        arenas.forEachIndexed { index, arena ->
            val item = ItemBuilder.from(Material.PAPER)
                .name(plugin.miniMessage.deserialize(arena.getName()))
                .lore(plugin.miniMessage.deserialize(arena.getDescription()))
                .asGuiItem {
                    fireworkWarsData.getArenaJoinCommand().executeJoinForPlayer(
                        it.whoClicked as Player, arena.getArenaNumber())
                }

            gui.setItem(index, item)
        }

        gui.open(event.player)
    }
}