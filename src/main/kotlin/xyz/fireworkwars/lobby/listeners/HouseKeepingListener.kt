package xyz.fireworkwars.lobby.listeners

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import foundation.esoteric.fireworkwarscore.interfaces.Event
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin

class HouseKeepingListener(private val plugin: FireworkWarsLobbyPlugin) : Event {
    private val lobbyWorld = plugin.configManager.lobbyConfig.getWorld()

    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (lobbyWorld == event.entity.world) {
            event.isCancelled = !plugin.isBuildModeEnabled()
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (lobbyWorld == event.block.world) {
            event.isCancelled = !plugin.isBuildModeEnabled()
        }
    }

    @EventHandler
    fun onExplosion(event: EntityExplodeEvent) {
        val location = event.location
        val world = location.world

        if (lobbyWorld == world) {
            world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 1, 0.0, 0.0, 0.0)
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)

            event.isCancelled = true

            if (plugin.isBuildModeEnabled()) {
                plugin.logLoudly("Bro get the FUCK back to building", true)
            }
        }
    }

    @EventHandler
    fun onExplosion(event: BlockExplodeEvent) {
        val location = event.block.location
        val world = location.world

        if (lobbyWorld == world) {
            world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 1, 0.0, 0.0, 0.0)
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)

            event.isCancelled = true
        }
    }

    @EventHandler
    fun onExperienceGain(event: PlayerPickupExperienceEvent) {
        val world = event.player.world

        if (lobbyWorld == world) {
            event.experienceOrb.remove()
            event.isCancelled = !plugin.isBuildModeEnabled()
        }
    }

    @EventHandler
    fun onPlayerPickUpItem(event: EntityPickupItemEvent) {
        val world = event.entity.world

        if (lobbyWorld == world) {
            event.isCancelled = !plugin.isBuildModeEnabled()
        }
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val world = event.player.world

        if (lobbyWorld == world) {
            event.isCancelled = !plugin.isBuildModeEnabled()
        }
    }
}