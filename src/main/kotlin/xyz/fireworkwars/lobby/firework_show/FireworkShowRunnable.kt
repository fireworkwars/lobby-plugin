package xyz.fireworkwars.lobby.firework_show

import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import xyz.fireworkwars.core.util.Cuboid
import xyz.fireworkwars.core.util.FireworkCreator
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin

class FireworkShowRunnable(private val plugin: FireworkWarsLobbyPlugin) : BukkitRunnable() {
    private val fireworkShowConfig = plugin.configManager.lobbyConfig.fireworkShow
    private val spawn = plugin.configManager.lobbyConfig.spawnLocation.toBukkit()

    private var ticksUntilStart = getNextFireworkShowTicks()
    private var ticksUntilEnd = 0

    private var waiting = true

    private val cuboid = Cuboid(fireworkShowConfig.corner1.toBukkit(), fireworkShowConfig.corner2.toBukkit())

    override fun run() {
        if (waiting) {
            if (ticksUntilStart-- <= 0) {
                waiting = false
                ticksUntilEnd = fireworkShowConfig.duration
            }
        } else {
            if (ticksUntilEnd-- <= 0) {
                waiting = true
                ticksUntilStart = getNextFireworkShowTicks()
            }

            spawnFirework()
        }
    }

    fun start() {
        runTaskTimer(plugin, 0, 1)
    }

    private fun getNextFireworkShowTicks(): Int {
        return fireworkShowConfig.interval + (0..fireworkShowConfig.randomness).random()
    }

    private fun spawnFirework() {
        val location = getRandomLocation()

        FireworkCreator.sendSupplyDropFirework(location, (40..70).random())
    }

    private fun getRandomLocation(): Location {
        val randomX = (cuboid.minX..cuboid.maxX).random().toDouble()
        val randomY = (cuboid.minY..cuboid.maxY).random().toDouble()
        val randomZ = (cuboid.minZ..cuboid.maxZ).random().toDouble()

        val location = Location(cuboid.world, randomX, spawn.y, randomZ)

        if (location.distanceSquared(spawn) < 30 * 30) {
            return getRandomLocation()
        } else {
            location.y = randomY
            return location
        }
    }
}