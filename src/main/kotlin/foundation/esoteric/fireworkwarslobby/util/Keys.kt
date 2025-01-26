package foundation.esoteric.fireworkwarslobby.util

import org.bukkit.NamespacedKey

class Keys {
    companion object {
        val LEADERBOARD_DISPLAY_ID = fromString("leaderboard_display")

        fun fromString(key: String): NamespacedKey {
            return NamespacedKey("firework-wars-lobby", key)
        }
    }
}