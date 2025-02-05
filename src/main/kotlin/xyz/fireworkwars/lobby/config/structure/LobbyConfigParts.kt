package xyz.fireworkwars.lobby.config.structure

import xyz.fireworkwars.core.language.Message
import org.bukkit.Bukkit
import xyz.fireworkwars.lobby.leaderboard.LeaderboardDisplay

data class EntityLocation(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
) {
    fun toBukkit() = org.bukkit.Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
}

data class NPCData(
    val name: String,
    val subtitle: String,
    val menu: NPCMenu,
    val location: EntityLocation,
    val skin: NPCSkin
)

data class NPCMenu(
    val title: String,
    val mapType: MapType
)

enum class MapType {
    BARRACKS,
    TOWN
}

data class NPCSkin(
    val value: String,
    val signature: String
)

data class LeaderboardData(
    val location: EntityLocation,
    val type: LeaderboardDisplay.LeaderboardType,
    val titleMessage: Message
)