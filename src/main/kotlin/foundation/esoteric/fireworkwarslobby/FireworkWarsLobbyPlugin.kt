package foundation.esoteric.fireworkwarslobby

import foundation.esoteric.fireworkwarscore.FireworkWarsCorePlugin
import foundation.esoteric.fireworkwarscore.language.LanguageManager
import foundation.esoteric.fireworkwarscore.profiles.PlayerDataManager
import foundation.esoteric.fireworkwarslobby.communication.LobbyPluginDataHolder
import foundation.esoteric.fireworkwarslobby.config.ConfigManager
import foundation.esoteric.fireworkwarslobby.leaderboard.LeaderboardManager
import foundation.esoteric.fireworkwarslobby.listeners.*
import foundation.esoteric.fireworkwarslobby.nametags.NameTagManager
import foundation.esoteric.fireworkwarslobby.npc.NPCManager
import foundation.esoteric.fireworkwarslobby.scoreboards.LobbyScoreboardManager
import net.kyori.adventure.text.Component.text
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class FireworkWarsLobbyPlugin : JavaPlugin() {
    lateinit var core: FireworkWarsCorePlugin

    lateinit var playerDataManager: PlayerDataManager
    lateinit var languageManager: LanguageManager

    lateinit var configManager: ConfigManager
    lateinit var npcManager: NPCManager
    lateinit var lobbyScoreboardManager: LobbyScoreboardManager
    lateinit var nameTagManager: NameTagManager
    lateinit var leaderboardManager: LeaderboardManager

    lateinit var playerConnectionListener: PlayerConnectionListener
    private val listeners = mutableListOf<Listener>()

    @Suppress("UnstableApiUsage")
    override fun onLoad() {
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-= Firework Wars Lobby Plugin =-=-=-=-=-=-=-=-=-=-=-=-=-=")
        logger.info("This is the start of Firework Wars Lobby Plugin logs.")
        logger.info("Info: v" + pluginMeta.version + " by " + pluginMeta.website)
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-= End of Plugin Info =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=")

        logger.info("Enabling Firework Wars Lobby Systems...")
        logger.info("Reading configuration files...")

        this.configManager = ConfigManager(this).apply { loadLobbyFromConfig() }

        logger.info("Successfully loaded configuration files.")
    }

    override fun onEnable() {
        logger.info("Starting Lobby initialisation sequence...")
        logger.info("Connecting to Firework Wars Core...")

        this.core = server.pluginManager.getPlugin("FireworkWarsCore") as FireworkWarsCorePlugin

        this.playerDataManager = core.playerDataManager
        this.languageManager = core.languageManager

        logger.info("Successfully connected to Firework Wars Core.")
        logger.info("Writing data to core...")

        core.lobbyPluginData = LobbyPluginDataHolder(this)

        logger.info("Finished writing data to core.")
        logger.info("Initialising NPC Manager...")

        this.npcManager = NPCManager(this).apply { spawnNPCs() }

        logger.info("Successfully deployed NPCs.")
        logger.info("Initialising Scoreboard Manager...")

        this.lobbyScoreboardManager = LobbyScoreboardManager(this)

        logger.info("Successfully initialised Scoreboard Manager.")
        logger.info("Initialising Nametag Manager...")

        this.nameTagManager = NameTagManager(this).apply { register() }

        logger.info("Successfully initialised Nametag Manager.")
        logger.info("Initialising Leaderboard Manager...")

        this.leaderboardManager = LeaderboardManager(this)

        logger.info("Successfully initialised Leaderboard Manager.")
        logger.info("Registering event listeners...")

        this.playerConnectionListener = PlayerConnectionListener(this).apply { register() }

        listeners.add(playerConnectionListener)
        listeners.add(NPCInteractListener(this).apply { register() })
        listeners.add(HouseKeepingListener(this).apply { register() })
        listeners.add(PlayerWorldChangeListener(this).apply { register() })
        listeners.add(PlayerChatListener(this).apply { register() })

        logger.info("Completed registration of event listeners.")
        logger.info("Listening to ${listeners.size} events.")

        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-= Firework Wars Lobby Plugin =-=-=-=-=-=-=-=-=-=-=-=-=-=")
        logger.info("End of logs for Firework Wars Lobby Plugin.")
        logger.info("Finished Firework Wars Lobby initialisation sequence.")
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-= All systems operational  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=")
    }

    fun registerEvent(event: Listener) {
        server.pluginManager.registerEvents(event, this)
    }

    fun isDebugging(): Boolean {
        return core.isDebugging
    }

    fun isBuildModeEnabled(): Boolean {
        return core.isBuildModeEnabled
    }

    fun runTaskLater(task: Runnable, delay: Long) {
        server.scheduler.runTaskLater(this, task, delay)
    }

    fun runTaskOneTickLater(task: Runnable) {
        this.runTaskLater(task, 1L)
    }

    fun runTaskTimer(task: Runnable, delay: Long, period: Long) {
        server.scheduler.runTaskTimer(this, task, delay, period)
    }

    fun logLoudly(message: String, force: Boolean = false) {
        if (isDebugging() || force) {
            server.broadcast(text(message))
        }
    }
}
