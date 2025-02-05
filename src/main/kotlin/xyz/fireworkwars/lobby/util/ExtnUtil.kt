package xyz.fireworkwars.lobby.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

@Suppress("unused")
fun Component.length(): Int {
    val mmString = MiniMessage.miniMessage().serialize(this)
    val withoutTags = mmString.replace(Regex("<[^>]*>"), "")

    return withoutTags.length
}