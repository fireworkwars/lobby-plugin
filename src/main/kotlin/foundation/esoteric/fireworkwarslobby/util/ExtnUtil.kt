package foundation.esoteric.fireworkwarslobby.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun Component.length(): Int {
    val mm = MiniMessage.miniMessage()
    val mmString = mm.serialize(this)

    val withoutTags = mmString.replace(Regex("<[^>]*>"), "")
    return withoutTags.length
}