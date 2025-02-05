package xyz.fireworkwars.lobby.npc.gui

import foundation.esoteric.fireworkwarscore.communication.Arena
import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.libs.gui.builder.item.ItemBuilder
import foundation.esoteric.fireworkwarscore.libs.gui.guis.Gui
import foundation.esoteric.fireworkwarscore.util.format
import foundation.esoteric.fireworkwarscore.util.playSound
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import xyz.fireworkwars.lobby.config.structure.MapType
import xyz.fireworkwars.lobby.config.structure.NPCMenu

class ArenaGUI(private val plugin: FireworkWarsLobbyPlugin, private val data: NPCMenu, private val player: Player) {
    private val fireworkWarsData = plugin.core.fireworkWarsPluginData
    private val languageManager = plugin.languageManager

    private val gui = Gui.gui()
        .title(data.title.format())
        .rows(3)
        .create()

    init {
        this.createForPlayer()
        this.fillArenaJoinItems()
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

        val filtered = arenas.filter(Arena::isAvailable)

        if (filtered.isEmpty()) {
            return this.setNoArenasItem()
        }

        filtered.forEachIndexed(this::setArenaJoinItem)
    }

    private fun setNoArenasItem() {
        val item = ItemBuilder.from(Material.GUNPOWDER)
            .name(languageManager.getMessage(Message.NO_ARENAS_AVAILABLE, player))
            .lore(*languageManager.getMessages(Message.NO_ARENAS_AVAILABLE_LORE, player))
            .asGuiItem()

        gui.setItem(0, item)
    }

    private fun setArenaJoinItem(index: Int, arena: Arena) {
        val currentPlayers = arena.getCurrentPlayers()
        val maxPlayers = arena.getMaxPlayers()

        val playerCountMessage = languageManager.getMessage(
            Message.CURRENT_PLAYERS, player, currentPlayers, maxPlayers
        )

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