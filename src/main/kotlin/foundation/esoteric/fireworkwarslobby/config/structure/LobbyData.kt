package foundation.esoteric.fireworkwarslobby.config.structure

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

data class LobbyData (
    val worldName: String,
    val spawnLocation: EntityLocation,
    val fireworkLocations: List<EntityLocation>,
    val npcs: List<NPCData>
) {
    fun getWorld(): World {
        return Bukkit.getWorld(worldName)!!
    }

    fun randomFireworkLocations(): List<Location> {
        val fireworkLocs = fireworkLocations.map { it.toBukkit() }
        val spawnLoc = spawnLocation.toBukkit()

        val randomFireworks = fireworkLocs.shuffled().take(3)
        return randomFireworks + spawnLoc
    }
}