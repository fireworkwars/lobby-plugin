package xyz.fireworkwars.lobby.npc

import xyz.fireworkwars.lobby.FireworkWarsLobbyPlugin

class NPCManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val npcs = mutableMapOf<Int, NPC>()
    val npcList = mutableListOf<NPC>()

    fun spawnNPCs() {
        plugin.configManager.lobbyConfig.npcs.forEach {
            val npc = NPC(plugin, it)
            this.registerNPC(npc)
        }
    }

    fun getNPC(id: Int): NPC? {
        return npcs[id]
    }

    private fun registerNPC(npc: NPC) {
        this.npcs[npc.id] = npc
        npcList.add(npc)

        plugin.runTaskTimer(0L, 1L, npc::lookAtEachPlayer)
    }
}