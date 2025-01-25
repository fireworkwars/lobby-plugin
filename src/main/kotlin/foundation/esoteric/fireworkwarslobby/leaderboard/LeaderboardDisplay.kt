package foundation.esoteric.fireworkwarslobby.leaderboard

import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.profiles.PlayerProfile
import foundation.esoteric.fireworkwarscore.util.NMSUtil
import foundation.esoteric.fireworkwarscore.util.getMessage
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import foundation.esoteric.fireworkwarslobby.config.structure.EntityLocation
import foundation.esoteric.fireworkwarslobby.util.PacketUtil
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.EntityType
import org.bukkit.entity.Player

class LeaderboardDisplay(private val plugin: FireworkWarsLobbyPlugin, private val location: EntityLocation, private val owner: Player) {
    private val handle: TextDisplay

    lateinit var title: Component
    lateinit var subtitle: Component
    private val entries = mutableListOf<PlayerProfile>()

    lateinit var valueFunction: (profile: PlayerProfile) -> Int

    init {
        this.handle = this.createLeaderboard()
    }

    private fun createLeaderboard(): TextDisplay {
        val display = TextDisplay(EntityType.TEXT_DISPLAY, NMSUtil.toNMSWorld(location.toBukkit().world))
        val bukkit = display.bukkitEntity as org.bukkit.entity.TextDisplay

        display.setPos(location.x, location.y, location.z)

        bukkit.alignment = org.bukkit.entity.TextDisplay.TextAlignment.LEFT
        bukkit.billboard = org.bukkit.entity.Display.Billboard.VERTICAL

        return display
    }

    private fun updateText() {
        val bukkit = handle.bukkitEntity as org.bukkit.entity.TextDisplay

        val header = title
            .appendNewline().appendNewline()
            .append(subtitle)
            .appendNewline().appendNewline()

        var text = header

        entries.clear()
        entries.addAll(plugin.server.onlinePlayers.map { plugin.playerDataManager.getPlayerProfile(it) })

        entries.sortedByDescending { valueFunction(it) }.forEachIndexed { index, profile ->
            text = text.append(owner.getMessage(
                Message.LEADERBOARD_ENTRY, index + 1, profile.formattedName(), valueFunction(profile)))
        }

        bukkit.text(text)
    }

    fun updateAndSendPackets() {
        if (!owner.isOnline) return

        this.updateText()

        val connection: ServerGamePacketListenerImpl = NMSUtil.toNMSEntity<ServerPlayer>(owner).connection

        connection.send(PacketUtil.getEntityAddPacket(handle))
        connection.send(ClientboundSetEntityDataPacket(handle.id, handle.entityData.packAll()))
    }
}