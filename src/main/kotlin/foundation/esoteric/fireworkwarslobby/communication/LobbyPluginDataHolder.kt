package foundation.esoteric.fireworkwarslobby.communication

import foundation.esoteric.fireworkwarscore.communication.LobbyPluginData
import foundation.esoteric.fireworkwarslobby.config.structure.LobbyData
import org.bukkit.Location
import org.bukkit.World

class LobbyPluginDataHolder(private val config: LobbyData) : LobbyPluginData {
    override fun getLobbySpawn(): Location {
        return config.spawnLocation.toBukkit()
    }

    override fun isLobby(world: World): Boolean {
        return world == config.getWorld()
    }
}