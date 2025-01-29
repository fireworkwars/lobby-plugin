package foundation.esoteric.fireworkwarslobby.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun Component.length(): Int {
    val mmString = MiniMessage.miniMessage().serialize(this)
    val withoutTags = mmString.replace(Regex("<[^>]*>"), "")

    return withoutTags.length
}