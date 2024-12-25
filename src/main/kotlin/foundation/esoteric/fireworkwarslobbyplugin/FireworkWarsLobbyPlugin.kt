package foundation.esoteric.fireworkwarslobbyplugin

import foundation.esoteric.fireworkwarslobbyplugin.config.ConfigManager
import foundation.esoteric.fireworkwarslobbyplugin.listeners.PlayerJoinListener
import org.bukkit.plugin.java.JavaPlugin

class FireworkWarsLobbyPlugin : JavaPlugin() {
    lateinit var configManager: ConfigManager

    @Suppress("UnstableApiUsage")
    override fun onLoad() {
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-= Firework Wars Lobby Plugin =-=-=-=-=-=-=-=-=-=-=-=-=-=")
        logger.info("This is the start of Firework Wars Lobby Plugin logs.")
        logger.info("Info: v" + pluginMeta.version + " by " + pluginMeta.website)
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-= End of Plugin Info =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=")

        logger.info("Enabling Firework Wars Lobby Systems...")
        logger.info("Reading configuration files...")

        configManager = ConfigManager(this)
        configManager.loadLobbyFromConfig()

        logger.info("Successfully loaded configuration files.")
    }

    override fun onEnable() {
        logger.info("Registering event listeners...")

        PlayerJoinListener(this).register()

        logger.info("Completed registration of event listeners.")

        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-= Firework Wars Lobby Plugin =-=-=-=-=-=-=-=-=-=-=-=-=-=")
        logger.info("End of logs for Firework Wars Lobby Plugin.")
        logger.info("Finished Firework Wars Lobby initialisation sequence.")
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-= All systems operational  =-=-=-=-=-=-=-=-=-=-=-=-=")
    }
}
