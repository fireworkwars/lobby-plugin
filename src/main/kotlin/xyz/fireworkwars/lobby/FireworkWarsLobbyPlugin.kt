package xyz.fireworkwars.lobby

import net.kyori.adventure.text.Component.text
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import xyz.fireworkwars.core.FireworkWarsCorePlugin
import xyz.fireworkwars.core.language.LanguageManager
import xyz.fireworkwars.core.profiles.PlayerDataManager
import xyz.fireworkwars.lobby.communication.LobbyPluginHook
import xyz.fireworkwars.lobby.config.manager.ConfigManager
import xyz.fireworkwars.lobby.firework_show.FireworkShowRunnable
import xyz.fireworkwars.lobby.leaderboard.LeaderboardManager
import xyz.fireworkwars.lobby.listeners.*
import xyz.fireworkwars.lobby.nametags.NameTagManager
import xyz.fireworkwars.lobby.npc.NPCManager
import xyz.fireworkwars.lobby.scoreboards.LobbyScoreboardManager

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

        this.configManager = ConfigManager(this).apply { this.loadLobbyFromConfig() }

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

        core.lobbyHook = LobbyPluginHook(this)

        logger.info("Finished writing data to core.")
        logger.info("Initialising NPC Manager...")

        this.npcManager = NPCManager(this).apply { this.spawnNPCs() }

        logger.info("Successfully deployed NPCs.")
        logger.info("Initialising Scoreboard Manager...")

        this.lobbyScoreboardManager = LobbyScoreboardManager(this)

        logger.info("Successfully initialised Scoreboard Manager.")
        logger.info("Initialising Nametag Manager...")

        this.nameTagManager = NameTagManager(this).apply { this.register() }

        logger.info("Successfully initialised Nametag Manager.")
        logger.info("Initialising Leaderboard Manager...")

        this.leaderboardManager = LeaderboardManager(this)

        logger.info("Successfully initialised Leaderboard Manager.")
        logger.info("Registering event listeners...")

        this.playerConnectionListener = PlayerConnectionListener(this).apply { this.register() }

        listeners.add(playerConnectionListener)
        listeners.add(NPCInteractListener(this).apply { this.register() })
        listeners.add(LeaderboardInteractListener(this).apply { this.register() })
        listeners.add(HouseKeepingListener(this).apply { this.register() })
        listeners.add(PlayerWorldChangeListener(this).apply { this.register() })
        listeners.add(PlayerChatListener(this).apply { this.register() })
        listeners.add(PlayerFallEvent(this).apply { this.register() })
        listeners.add(ServerListPingListener(this).apply { this.register() })

        logger.info("Completed registration of event listeners.")
        logger.info("Listening to ${listeners.size} events.")

        logger.info("Starting firework show...")

        FireworkShowRunnable(this).start()

        logger.info("Firework show started.")

        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-= Firework Wars Lobby Plugin =-=-=-=-=-=-=-=-=-=-=-=-=-=")
        logger.info("End of logs for Firework Wars Lobby Plugin.")
        logger.info("Finished Firework Wars Lobby initialisation sequence.")
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-= All systems operational  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=")
    }

    override fun onDisable() {
        super.onDisable()

        nameTagManager.removeAllNameTags()
    }

    fun registerEvent(event: Listener) {
        server.pluginManager.registerEvents(event, this)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun isDebugging(): Boolean {
        return core.isDebugging
    }

    fun isBuildModeEnabled(): Boolean {
        return core.isBuildModeEnabled
    }

    fun runTaskLater(delay: Long, task: Runnable) {
        server.scheduler.runTaskLater(this, task, delay)
    }

    fun runTaskOneTickLater(task: Runnable) {
        this.runTaskLater(1L, task)
    }

    fun runTaskTimer(delay: Long, period: Long, task: Runnable) {
        server.scheduler.runTaskTimer(this, task, delay, period)
    }

    fun logLoudly(message: String, force: Boolean = false) {
        if (this.isDebugging() || force) {
            server.broadcast(text(message))
        }
    }
}
