package xyz.fireworkwars.lobby.nametags

import org.bukkit.entity.Display
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import xyz.fireworkwars.core.interfaces.Event
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import java.util.*

class NameTagManager(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val playerDataManager = plugin.playerDataManager

    private val nameTags: MutableMap<UUID, TextDisplay> = mutableMapOf()
    private val nameTagVisibility: MutableMap<UUID, Boolean> = mutableMapOf()

    override fun register() {
        plugin.registerEvent(this)
    }

    fun setNameTagVisible(player: Player, visible: Boolean) {
        this.nameTagVisibility[player.uniqueId] = visible
        nameTags[player.uniqueId]?.isVisibleByDefault = visible
    }

    fun removeAllNameTags() {
        nameTags.values.forEach(TextDisplay::remove)
        nameTags.clear()
        nameTagVisibility.clear()
    }

    private fun createNameTag(player: Player) {
        val profile = playerDataManager.getPlayerProfile(player)
        val world = player.world
        val location = player.location

        val display = world.spawn(location, TextDisplay::class.java)

        display.text(profile.formattedName())
        display.isVisibleByDefault = nameTagVisibility[player.uniqueId] ?: true

        display.alignment = TextDisplay.TextAlignment.CENTER
        display.billboard = Display.Billboard.VERTICAL
        display.transformation = display.transformation.apply {
            translation.set(0.0, 0.2, 0.0)
        }

        player.hideEntity(plugin, display)

        player.passengers.forEach(Entity::remove)
        player.addPassenger(display)

        this.nameTags[player.uniqueId] = display
        this.nameTagVisibility[player.uniqueId] = display.isVisibleByDefault
    }

    private fun removeNameTag(player: Player) {
        val display = nameTags[player.uniqueId] ?: return

        display.vehicle?.removePassenger(display)
        display.remove()

        nameTags.remove(player.uniqueId)
    }

    private fun removeVisibilityData(player: Player) {
        nameTagVisibility.remove(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        this.createNameTag(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        this.removeNameTag(event.player)
        this.removeVisibilityData(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        this.removeNameTag(event.entity)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        this.createNameTag(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        this.removeNameTag(event.player)

        plugin.runTaskOneTickLater {
            if (event.player.isConnected) {
                this.createNameTag(event.player)
            }
        }
    }
}