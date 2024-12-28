package foundation.esoteric.fireworkwarslobby.npc

import com.destroystokyo.paper.profile.CraftPlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import foundation.esoteric.fireworkwarscore.util.NMSUtil
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import foundation.esoteric.fireworkwarslobby.config.structure.NPCData
import foundation.esoteric.fireworkwarslobby.npc.connection.EmptyConnection
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.*
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.CommonListenerCookie
import net.minecraft.server.network.ServerGamePacketListenerImpl
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import java.util.*

class NPC(private val plugin: FireworkWarsLobbyPlugin, val data: NPCData) {
    private val location = data.location.toBukkit()
    private val world = location.world

    private val nmsWorld = NMSUtil.toNMSWorld(world)
    private val handle: ServerPlayer
    val id: Int

    private val nameTag: TextDisplay

    init {
        this.handle = createServerPlayer()
        this.id = handle.id
        this.nameTag = createNameTag()
    }

    private fun createServerPlayer(): ServerPlayer {
        val profile: CraftPlayerProfile = plugin.server.createProfile(UUID.randomUUID()) as CraftPlayerProfile
        profile.properties.add(
            ProfileProperty("textures", data.skin.value, data.skin.signature))

        val npc = ServerPlayer(nmsWorld.server!!, nmsWorld as ServerLevel, profile.gameProfile, ClientInformation.createDefault())
        val entityData: SynchedEntityData = npc.entityData

        val bitmask = (0x01 or 0x04 or 0x08 or 0x10 or 0x20 or 0x40).toByte()
        entityData.set(EntityDataAccessor(17, EntityDataSerializers.BYTE), bitmask)

        npc.connection = ServerGamePacketListenerImpl(
            npc.server, EmptyConnection(null), npc, CommonListenerCookie.createInitial(npc.getGameProfile(), false))

        return npc
    }

    private fun createNameTag(): TextDisplay {
        val display = world.spawn(location, TextDisplay::class.java)

        display.text(plugin.mm.deserialize(data.name))
        display.alignment = TextDisplay.TextAlignment.CENTER
        display.billboard = Display.Billboard.VERTICAL
        display.transformation.translation.let {
            it.x = 0.0F
            it.y = 0.2F
            it.z = 0.0F
        }

        handle.bukkitEntity.addPassenger(display)
        return display
    }

    fun sendInitPackets(player: Player) {
        val connection: ServerGamePacketListenerImpl = NMSUtil.toNMSEntity<ServerPlayer>(player).connection

        connection.send(ClientboundPlayerInfoUpdatePacket(Action.ADD_PLAYER, handle))
        connection.send(ClientboundAddEntityPacket(handle, 0, BlockPos(location.blockX, location.blockY, location.blockZ)))
        connection.send(ClientboundSetEntityDataPacket(id, handle.entityData.packAll()!!))

        plugin.runTaskLater({
            connection.send(ClientboundPlayerInfoRemovePacket(listOf(handle.uuid)))
        }, 20L)
    }

    fun runLookAtTask() {
        val nearest = world.players.minByOrNull { it.location.distanceSquared(location) } ?: return

        handle.lookAt(
            EntityAnchorArgument.Anchor.EYES, NMSUtil.toNMSEntity(nearest), EntityAnchorArgument.Anchor.EYES)

        world.players.forEach {
            updateRotationPackets(it, handle.yHeadRot, handle.xRot)
        }
    }

    private fun updateRotationPackets(player: Player, yaw: Float, pitch: Float) {
        val connection: ServerGamePacketListenerImpl = NMSUtil.toNMSEntity<ServerPlayer>(player).connection

        connection.send(ClientboundRotateHeadPacket(handle, yaw.toInt().toByte()))
        connection.send(ClientboundMoveEntityPacket.Rot(
            id,
            (handle.yRot * 256 / 360).toInt().toByte(),
            (handle.xRot * 256 / 360).toInt().toByte(),
            handle.onGround()
        ))
    }
}