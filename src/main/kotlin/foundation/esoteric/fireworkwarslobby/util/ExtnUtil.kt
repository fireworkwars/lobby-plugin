package foundation.esoteric.fireworkwarslobby.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage

fun Component.length(): Int {
    val decorationsMap = this.decorations().map { it.key to TextDecoration.State.NOT_SET }.toMap()
    val withoutDecorations = this.compact().decorations(decorationsMap)
    val withoutEvents = withoutDecorations.clickEvent(null).hoverEvent(null)

    return MiniMessage.miniMessage().serialize(withoutEvents).length
}