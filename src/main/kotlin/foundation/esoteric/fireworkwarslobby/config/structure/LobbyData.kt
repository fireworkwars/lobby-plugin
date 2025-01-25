package foundation.esoteric.fireworkwarslobby.config.structure

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

data class LobbyData (
    val worldName: String,
    val spawnLocation: EntityLocation,
    val fireworkLocations: List<EntityLocation>,
    val npcs: List<NPCData>,
    val leaderboards: LeaderboardData
) {
    fun getWorld(): World {
        return Bukkit.getWorld(worldName)!!
    }

    fun randomFireworkLocations(): List<Location> {
        return fireworkLocations
            .map(EntityLocation::toBukkit)
            .shuffled()
            .take(4)
    }
}