package foundation.esoteric.fireworkwarslobby.leaderboard

import foundation.esoteric.fireworkwarscore.language.Message
import foundation.esoteric.fireworkwarscore.profiles.PlayerProfile
import foundation.esoteric.fireworkwarscore.util.NMSUtil
import foundation.esoteric.fireworkwarscore.util.getMessage
import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin
import foundation.esoteric.fireworkwarslobby.config.structure.LeaderboardData
import foundation.esoteric.fireworkwarslobby.util.PacketUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Interaction
import org.bukkit.entity.Player

class LeaderboardDisplay(private val data: LeaderboardData, plugin: FireworkWarsLobbyPlugin, private val owner: Player) {
    private val playerDataManager = plugin.playerDataManager
    private val mm = MiniMessage.builder().strict(true).build()

    private val location = data.location

    private val body: TextDisplay
    private val header: TextDisplay
    private val interaction: Interaction

    val id: Int

    private val entries = mutableListOf<PlayerProfile>()

    private var title: Component = Component.empty()
    private var subtitle: Component = Component.empty()

    private val type = data.type
    var timePeriod = LeaderboardTime.ALL_TIME

    init {
        this.body = this.createLeaderboardBody()
        this.header = this.createLeaderboardHeader()
        this.interaction = this.createLeaderboardInteraction()
        this.id = interaction.id
    }

    private fun createLeaderboardBody(): TextDisplay {
        val display = TextDisplay(EntityType.TEXT_DISPLAY, NMSUtil.toNMSWorld(location.toBukkit().world))
        val bukkit = display.bukkitEntity as org.bukkit.entity.TextDisplay

        display.setPos(location.x, location.y, location.z)

        bukkit.alignment = org.bukkit.entity.TextDisplay.TextAlignment.LEFT
        bukkit.billboard = org.bukkit.entity.Display.Billboard.VERTICAL

        return display
    }

    private fun createLeaderboardHeader(): TextDisplay {
        val display = TextDisplay(EntityType.TEXT_DISPLAY, NMSUtil.toNMSWorld(location.toBukkit().world))
        val bukkit = display.bukkitEntity as org.bukkit.entity.TextDisplay

        display.startRiding(body)

        bukkit.alignment = org.bukkit.entity.TextDisplay.TextAlignment.CENTER
        bukkit.billboard = org.bukkit.entity.Display.Billboard.VERTICAL

        return display
    }

    private fun createLeaderboardInteraction(): Interaction {
        val interaction = Interaction(EntityType.INTERACTION, NMSUtil.toNMSWorld(location.toBukkit().world))
        val bukkit = interaction.bukkitEntity as org.bukkit.entity.Interaction

        interaction.setPos(location.x, location.y, location.z)

        bukkit.interactionHeight = 3.0F

        return interaction
    }

    private fun updateTitle() {
        val bukkit = header.bukkitEntity as org.bukkit.entity.TextDisplay

        this.title = owner.getMessage(data.titleMessage)
        this.subtitle = owner.getMessage(timePeriod.message())

        val text = mm.deserialize("${mm.serialize(title)}<br/><br/>${mm.serialize(subtitle)}<br/><br/>")
        bukkit.text(text)
    }

    private fun updateEntries() {
        val bukkit = body.bukkitEntity as org.bukkit.entity.TextDisplay
        var text: Component = Component.empty()

        entries.clear()
        entries.addAll(playerDataManager.getAllProfiles())

        entries.sortedByDescending(this::getProfileValue).forEachIndexed { index, profile ->
            val place = index + 1
            val name = profile.formattedName()
            val value = this.getProfileValue(profile)

            text = text
                .append(owner.getMessage(Message.LEADERBOARD_ENTRY, place, name, value))
                .appendNewline()
        }

        bukkit.text(text)
    }

    private fun getProfileValue(profile: PlayerProfile): Int {
        val stats = when (timePeriod) {
            LeaderboardTime.DAILY -> profile.dailyStats
            LeaderboardTime.WEEKLY -> profile.weeklyStats
            LeaderboardTime.ALL_TIME -> profile.stats
        }

        return when (type) {
            LeaderboardType.WINS -> stats.wins
            LeaderboardType.KILLS -> stats.kills
        }
    }

    fun updateAndSendPackets() {
        if (!owner.isOnline) return

        this.updateTitle()
        this.updateEntries()

        val connection: ServerGamePacketListenerImpl = NMSUtil.toNMSEntity<ServerPlayer>(owner).connection

        connection.send(PacketUtil.getEntityAddPacket(body))
        connection.send(ClientboundSetEntityDataPacket(body.id, body.entityData.packAll()))

        connection.send(PacketUtil.getEntityAddPacket(header))
        connection.send(ClientboundSetEntityDataPacket(header.id, header.entityData.packAll()))

        connection.send(PacketUtil.getEntityAddPacket(interaction))
        connection.send(ClientboundSetEntityDataPacket(id, interaction.entityData.packAll()))
    }

    enum class LeaderboardType {
        WINS,
        KILLS
    }

    enum class LeaderboardTime {
        DAILY,
        WEEKLY,
        ALL_TIME;

        fun next(): LeaderboardTime {
            return when (this) {
                DAILY -> WEEKLY
                WEEKLY -> ALL_TIME
                ALL_TIME -> DAILY
            }
        }

        fun message(): Message {
            return when (this) {
                DAILY -> Message.LEADERBOARD_TIME_DAILY
                WEEKLY -> Message.LEADERBOARD_TIME_WEEKLY
                ALL_TIME -> Message.LEADERBOARD_TIME_ALL_TIME
            }
        }
    }
}