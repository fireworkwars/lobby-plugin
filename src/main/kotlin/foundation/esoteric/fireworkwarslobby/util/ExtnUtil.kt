package foundation.esoteric.fireworkwarslobby.util

import kotlin.math.max

fun String.matchLength(other: String): String {
    val difference = max(0, other.length - length)
    return this + " ".repeat(difference)
}