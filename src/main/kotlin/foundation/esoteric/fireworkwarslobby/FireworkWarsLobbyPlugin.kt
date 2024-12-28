package foundation.esoteric.fireworkwarslobby

import foundation.esoteric.fireworkwarscore.BasePlugin
import foundation.esoteric.fireworkwarscore.FireworkWarsCorePlugin
import foundation.esoteric.fireworkwarscore.language.LanguageManager
import foundation.esoteric.fireworkwarscore.profiles.PlayerDataManager
import foundation.esoteric.fireworkwarslobby.communication.LobbyPluginDataHolder
import foundation.esoteric.fireworkwarslobby.config.ConfigManager
import foundation.esoteric.fireworkwarslobby.listeners.NPCInteractListener
import foundation.esoteric.fireworkwarslobby.listeners.PlayerJoinListener
import foundation.esoteric.fireworkwarslobby.npc.NPCManager
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.Listener

class FireworkWarsLobbyPlugin : BasePlugin() {
    lateinit var core: FireworkWarsCorePlugin

class FireworkWarsLobbyPlugin : JavaPlugin() {
    lateinit var configManager: ConfigManager

    @Suppress("UnstableApiUsage")
    override fun onLoad() {
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-= Firework Wars Lobby Plugin =-=-=-=-=-=-=-=-=-=-=-=-=-=")
        logger.info("This is the start of Firework Wars Lobby Plugin logs.")
        logger.info("Info: v" + pluginMeta.version + " by " + pluginMeta.website)
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-= End of Plugin Info =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=")

        logger.info("Enabling Firework Wars Lobby Systems...")
        logger.info("Reading configuration files...")

        configManager = ConfigManager(this)
        configManager.loadLobbyFromConfig()

        logger.info("Successfully loaded configuration files.")
        logger.info("Connecting to Firework Wars Core...")

        this.core = server.pluginManager.getPlugin("FireworkWarsCore") as FireworkWarsCorePlugin

        this.playerDataManager = core.playerDataManager
        this.languageManager = core.languageManager

        logger.info("Successfully connected to Firework Wars Core.")
        logger.info("Writing data to core...")

        core.lobbyPluginData = LobbyPluginDataHolder(configManager.lobbyConfig)

        logger.info("Finished writing data to core.")
    }

    override fun onEnable() {
        logger.info("Starting Lobby initialisation sequence...")
        logger.info("Initialising NPC Manager...")

        this.npcManager = NPCManager(this).apply { spawnNPCs() }

        logger.info("Successfully deployed NPCs.")
        logger.info("Registering event listeners...")

        PlayerJoinListener(this).register()

        logger.info("Completed registration of event listeners.")
        logger.info("Listening to ${listeners.size} events.")

        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-= Firework Wars Lobby Plugin =-=-=-=-=-=-=-=-=-=-=-=-=-=")
        logger.info("End of logs for Firework Wars Lobby Plugin.")
        logger.info("Finished Firework Wars Lobby initialisation sequence.")
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-= All systems operational  =-=-=-=-=-=-=-=-=-=-=-=-=")
    }
}
