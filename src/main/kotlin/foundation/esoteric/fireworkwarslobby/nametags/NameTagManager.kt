package foundation.esoteric.fireworkwarslobby.nametags

import foundation.esoteric.fireworkwarscore.interfaces.Event
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

class NameTagManager(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val playerDataManager = plugin.playerDataManager

    private val nameTags: MutableMap<UUID, TextDisplay> = mutableMapOf()

    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun createNameTag(player: Player) {
        val profile = playerDataManager.getPlayerProfile(player)
        val world = player.world
        val location = player.location

        val display = world.spawn(location, TextDisplay::class.java)

        display.text(profile.formattedName())
        display.alignment = TextDisplay.TextAlignment.CENTER
        display.billboard = Display.Billboard.VERTICAL
        display.transformation = display.transformation.apply {
            translation.set(0.0, 0.2, 0.0)
        }

        player.addPassenger(display)
        nameTags[player.uniqueId] = display
    }

    private fun removeNameTag(player: Player) {
        val display = nameTags[player.uniqueId] ?: return

        display.vehicle?.eject()
        display.remove()
        nameTags.remove(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        this.createNameTag(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        this.removeNameTag(event.player)
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
        plugin.runTaskOneTickLater { this.createNameTag(event.player) }
    }
}