package com.daromi.torrent.nexus.util

fun b(value: Int): Byte = value.toByte()

fun Byte.char(): Char = this.toInt().toChar()

fun ByteArray.toCharArray(): CharArray = this.map { it.char() }.toCharArray()

fun ByteArray.indexOf(
    element: Byte,
    startIndex: Int,
): Int {
    if (startIndex >= this.size) {
        return -1
    }

    var index = -1

    for (i in startIndex..<this.size) {
        if (this[i] == element) {
            index = i
            break
        }
    }

    return index
}
