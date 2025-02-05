package xyz.fireworkwars.lobby.util

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

class PacketUtil {
    companion object {
        @JvmStatic
        fun getEntityAddPacket(entity: Entity): ClientboundAddEntityPacket {
            return ClientboundAddEntityPacket(
                entity.id, entity.uuid,
                entity.x, entity.y, entity.z,
                entity.xRot, entity.yRot,
                entity.type,
                0,
                Vec3(0.0, 0.0, 0.0),
                entity.yHeadRot.toDouble()
            )
        }
    }
}