package foundation.esoteric.fireworkwarslobby.npc.gui

import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.Gui
import foundation.esoteric.fireworkwarscore.communication.Arena
import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.util.format
import foundation.esoteric.fireworkwarscore.util.playSound
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import foundation.esoteric.fireworkwarslobby.config.structure.MapType
import foundation.esoteric.fireworkwarslobby.config.structure.NPCMenu
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent

class ArenaGUI(private val plugin: FireworkWarsLobbyPlugin, private val data: NPCMenu, private val player: Player) {
    private val fireworkWarsData = plugin.core.fireworkWarsPluginData
    private val languageManager = plugin.languageManager

    private val gui = Gui.gui()
        .title(data.title.format())
        .rows(3)
        .create()

    init {
        createForPlayer()
        fillArenaJoinItems()
    }

    fun show() {
        gui.open(player)
    }

    private fun createForPlayer() {
        gui.setOpenGuiAction {
            plugin.runTaskLater({ player.playSound(Sound.ITEM_CROSSBOW_QUICK_CHARGE_3) }, 2L)
            plugin.runTaskLater({ player.playSound(Sound.ITEM_CROSSBOW_LOADING_END) }, 10L)
        }

        gui.setDefaultClickAction {
            it.isCancelled = true

            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
        }
    }

    private fun fillArenaJoinItems() {
        val arenas = when (data.mapType) {
            MapType.BARRACKS -> fireworkWarsData.getBarracksArenas()
            MapType.TOWN -> fireworkWarsData.getTownArenas()
        }

        val filtered = arenas.filter {
            !it.isFull()
        }

        if (filtered.isEmpty()) {
            return setNoArenasItem()
        }

        filtered.forEachIndexed(this::setArenaJoinItem)
    }

    private fun setNoArenasItem() {
        val item = ItemBuilder.from(Material.GUNPOWDER)
            .name(languageManager.getMessage(Message.NO_ARENAS_AVAILABLE, player, *emptyArray()))
            .lore(languageManager.getMessage(Message.NO_ARENAS_AVAILABLE_LORE, player, *emptyArray()))
            .asGuiItem()

        gui.setItem(0, item)
    }

    private fun setArenaJoinItem(index: Int, arena: Arena) {
        val currentPlayers = arena.getCurrentPlayers()
        val maxPlayers = arena.getMaxPlayers()

        val playerCountMessage = languageManager.getMessage(
            Message.CURRENT_PLAYERS, player, currentPlayers, maxPlayers)

        val lore = arena.getDescription()
            .split("\n")
            .map(String::format)
            .toMutableList()
            .apply { add(playerCountMessage) }
            .toList()

        val item = ItemBuilder.from(Material.PAPER)
            .name(arena.getName().format())
            .lore(lore)
            .asGuiItem {
                fireworkWarsData.getArenaJoinCommand().executeJoinForPlayer(player, arena.getArenaNumber())
            }

        gui.setItem(index, item)
    }
}