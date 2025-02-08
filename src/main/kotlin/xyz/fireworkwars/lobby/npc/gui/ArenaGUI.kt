package xyz.fireworkwars.lobby.npc.gui

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import xyz.fireworkwars.core.communication.FireworkWarsServiceProvider.LiveArenaData
import xyz.fireworkwars.core.language.Message
import xyz.fireworkwars.core.libs.gui.builder.item.ItemBuilder
import xyz.fireworkwars.core.libs.gui.guis.Gui
import xyz.fireworkwars.core.util.format
import xyz.fireworkwars.core.util.playSound
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import xyz.fireworkwars.lobby.config.structure.MapType
import xyz.fireworkwars.lobby.config.structure.NPCMenu

class ArenaGUI(private val plugin: FireworkWarsLobbyPlugin, private val data: NPCMenu, private val player: Player) {
    private val fireworkWarsData = plugin.core.fireworkWarsServiceProvider
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

        val filtered = arenas.filter(LiveArenaData::isAvailable)

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

    private fun setArenaJoinItem(index: Int, arena: LiveArenaData) {
        val currentPlayers = arena.getCurrentPlayers()
        val maxPlayers = arena.getMaxPlayers()

        val playerCountMessage = languageManager.getMessage(Message.CURRENT_PLAYERS,
            player, currentPlayers, maxPlayers)

        val lore = arena.getDescription()
            .split("\n")
            .map(String::format)
            .toMutableList()
            .apply { this.add(playerCountMessage) }
            .toList()

        val item = ItemBuilder.from(Material.PAPER)
            .name(arena.getName().format())
            .lore(lore)
            .asGuiItem {
                fireworkWarsData.getArenaJoinExecutor().executeJoinForPlayer(player, arena.getArenaNumber())
            }

        gui.setItem(index, item)
    }
}