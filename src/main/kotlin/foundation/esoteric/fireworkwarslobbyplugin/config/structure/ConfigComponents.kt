package foundation.esoteric.fireworkwarslobbyplugin.config.structure

data class EntityLocation(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
)

data class NPCData(
    val name: String,
    val location: EntityLocation,
    val skin: NPCSkin
)

data class NPCSkin(
    val value: String,
    val signature: String
)