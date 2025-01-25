package foundation.esoteric.fireworkwarslobby.leaderboard

import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.profiles.PlayerProfile
import foundation.esoteric.fireworkwarscore.util.NMSUtil
import foundation.esoteric.fireworkwarscore.util.getMessage
import foundation.esoteric.fireworkwarslobby.config.structure.EntityLocation
import foundation.esoteric.fireworkwarslobby.util.PacketUtil
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.EntityType
import org.bukkit.entity.Player

class LeaderboardDisplay(private val location: EntityLocation) {
    private val handle: TextDisplay
    val id: Int

    lateinit var title: Component
    lateinit var subtitle: Component
    val entries = mutableListOf<PlayerProfile>()

    lateinit var displayValue: (profile: PlayerProfile) -> Int

    init {
        this.handle = this.createLeaderboard()
        this.id = handle.id
    }

    private fun createLeaderboard(): TextDisplay {
        val display = TextDisplay(EntityType.TEXT_DISPLAY, NMSUtil.toNMSWorld(location.toBukkit().world))
        val bukkit = display.bukkitEntity as org.bukkit.entity.TextDisplay

        display.setPos(location.x, location.y, location.z)

        bukkit.alignment = org.bukkit.entity.TextDisplay.TextAlignment.LEFT
        bukkit.billboard = org.bukkit.entity.Display.Billboard.VERTICAL

        return display
    }

    private fun updateTextFor(player: Player) {
        val bukkit = handle.bukkitEntity as org.bukkit.entity.TextDisplay

        val header = title
            .appendNewline()
            .append(subtitle)
            .appendNewline()

        var text = header

        entries.forEachIndexed { index, profile ->
            text = text.append(player.getMessage(
                Message.LEADERBOARD_ENTRY, index + 1, profile.formattedName(), displayValue(profile)))
        }

        bukkit.text(text)
    }

    fun updateAndSendPackets(player: Player) {
        this.updateTextFor(player)

        val connection: ServerGamePacketListenerImpl = NMSUtil.toNMSEntity<ServerPlayer>(player).connection

        connection.send(PacketUtil.getEntityAddPacket(handle))
        connection.send(ClientboundSetEntityDataPacket(id, handle.entityData.packAll()))
    }
}