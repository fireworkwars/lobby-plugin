package foundation.esoteric.fireworkwarslobby.config.structure

import org.bukkit.Bukkit
import org.bukkit.World

data class LobbyData (
    val worldName: String,
    val spawnLocation: EntityLocation,
    val npcs: List<NPCData>
) {
    fun getWorld(): World {
        return Bukkit.getWorld(worldName)!!
    }
}