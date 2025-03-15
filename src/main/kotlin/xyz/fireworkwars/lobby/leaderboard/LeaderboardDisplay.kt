package xyz.fireworkwars.lobby.leaderboard

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Interaction
import org.bukkit.entity.Player
import xyz.fireworkwars.core.language.Message
import xyz.fireworkwars.core.profiles.PlayerProfile
import xyz.fireworkwars.core.util.NMSUtil
import xyz.fireworkwars.core.util.getMessage
import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin
import xyz.fireworkwars.lobby.config.structure.LeaderboardData
import xyz.fireworkwars.lobby.util.PacketUtil
import java.util.concurrent.TimeUnit

class LeaderboardDisplay(
    private val data: LeaderboardData,
    private val plugin: FireworkWarsLobbyPlugin,
    private val owner: Player
) {
    private val lineWidth = 200
    private val interactionHeight = 2.0F

    private val playerDataManager = plugin.playerDataManager
    private val mm = MiniMessage.builder().strict(true).build()

    private val location = data.location
    private val level = NMSUtil.toNMSWorld(location.toBukkit().world)

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
        val display = TextDisplay(EntityType.TEXT_DISPLAY, level)
        val bukkit = display.bukkitEntity as org.bukkit.entity.TextDisplay

        display.setPos(location.x, location.y, location.z)

        bukkit.alignment = org.bukkit.entity.TextDisplay.TextAlignment.LEFT
        bukkit.billboard = org.bukkit.entity.Display.Billboard.VERTICAL
        bukkit.lineWidth = lineWidth
        bukkit.isSeeThrough = true

        return display
    }

    private fun createLeaderboardHeader(): TextDisplay {
        val display = TextDisplay(EntityType.TEXT_DISPLAY, level)
        val bukkit = display.bukkitEntity as org.bukkit.entity.TextDisplay

        display.setPos(location.x, location.y, location.z)
        display.startRiding(body)

        bukkit.alignment = org.bukkit.entity.TextDisplay.TextAlignment.CENTER
        bukkit.billboard = org.bukkit.entity.Display.Billboard.VERTICAL
        bukkit.lineWidth = lineWidth
        bukkit.isSeeThrough = true

        return display
    }

    private fun createLeaderboardInteraction(): Interaction {
        val interaction = Interaction(EntityType.INTERACTION, level)
        val bukkit = interaction.bukkitEntity as org.bukkit.entity.Interaction

        interaction.setPos(location.x, location.y, location.z)

        bukkit.interactionHeight = interactionHeight

        return interaction
    }

    private fun updateTitle() {
        val bukkit = header.bukkitEntity as org.bukkit.entity.TextDisplay

        this.title = owner.getMessage(data.titleMessage)
        this.subtitle = owner.getMessage(timePeriod.message())

        val text = mm.deserialize(
            "" +
                    "${mm.serialize(title)}<br/><br/>" +
                    "${mm.serialize(subtitle)}<br/>" +
                    "${mm.serialize(this.getResetMessage())}<br/>"
        )

        bukkit.text(text)
        bukkit.transformation = bukkit.transformation.apply {
            translation.set(0.0, 0.75 + entries.size * 0.25, 0.0)
        }
    }

    private fun updateEntries() {
        val bukkit = body.bukkitEntity as org.bukkit.entity.TextDisplay
        var text: Component = Component.newline()

        entries.clear()
        entries.addAll(playerDataManager.getAllProfiles())

        entries.sortedByDescending(this::getProfileValue).take(10).forEachIndexed { index, profile ->
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

    private fun getResetMessage(): Component {
        val nextResetTime = when (timePeriod) {
            LeaderboardTime.DAILY -> plugin.core.statResetScheduler.getNextDailyReset()
            LeaderboardTime.WEEKLY -> plugin.core.statResetScheduler.getNextWeeklyReset()
            LeaderboardTime.ALL_TIME -> return owner.getMessage(Message.LEADERBOARD_NEVER_RESETS)
        }

        val days = TimeUnit.MILLISECONDS.toDays(nextResetTime)
        val hours = TimeUnit.MILLISECONDS.toHours(nextResetTime) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(nextResetTime) % 60

        val daysPl = if (days == 1L) "" else "s"
        val hoursPl = if (hours == 1L) "" else "s"
        val minutesPl = if (minutes == 1L) "" else "s"

        return when {
            days > 0 -> owner.getMessage(Message.LEADERBOARD_RESETS_IN_DAYS, days, daysPl)
            hours > 0 -> owner.getMessage(Message.LEADERBOARD_RESETS_IN_HOURS, hours, hoursPl)
            minutes > 0 -> owner.getMessage(Message.LEADERBOARD_RESETS_IN_MINUTES, minutes, minutesPl)
            else -> owner.getMessage(Message.LEADERBOARD_RESETS_SOON)
        }
    }

    fun updateAndSendPackets() {
        if (!owner.isOnline) return

        this.updateEntries()
        this.updateTitle()

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