package foundation.esoteric.fireworkwarslobby.npc

import foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin

class NPCManager(private val plugin: FireworkWarsLobbyPlugin) {
    private val npcs = mutableMapOf<Int, NPC>()
    val npcList = mutableListOf<NPC>()

    fun spawnNPCs() {
        plugin.configManager.lobbyConfig.npcs.forEach {
            val npc = NPC(plugin, it)
            plugin.runTaskTimer(npc::runLookAtTask, 0L, 1L)

            registerNPC(npc)
        }
    }

    fun getNPC(id: Int): NPC {
        return npcs[id] ?: throw IllegalArgumentException("NPC with id $id not found")
    }

    private fun registerNPC(npc: NPC) {
        npcs[npc.id] = npc
        npcList.add(npc)
    }
}