package com.torrent.nexus.util

fun CharArray.toInt(): Int? {
    val base = 10

    var power = 1
    var total = 0

    for (i in this.size - 1 downTo 0) {
        try {
            total += this[i].digitToInt(base) * power
        } catch (_: IllegalArgumentException) {
            return null
        }

        power *= base
    }

    return total
}

fun CharArray.toLong(): Long? {
    val base = 10

    var power = 1L
    var total = 0L

    for (i in this.size - 1 downTo 0) {
        try {
            total += this[i].digitToInt(base) * power
        } catch (_: IllegalArgumentException) {
            return null
        }

        power *= base
    }

    return total
}
