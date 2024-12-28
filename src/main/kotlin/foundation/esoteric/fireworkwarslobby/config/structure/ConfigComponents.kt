package foundation.esoteric.fireworkwarslobbyplugin.config.structure

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
    val location: EntityLocation,
    val skin: NPCSkin
)

data class NPCSkin(
    val value: String,
    val signature: String
)