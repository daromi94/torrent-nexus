package com.bittorrent.client.shared

fun Char.byte(): Byte = this.code.toByte()

fun CharArray.toInt(): Result<Int> {
    val base = 10

    var power = 1
    var total = 0

    for (i in this.size - 1 downTo 0) {
        try {
            total += this[i].digitToInt(base) * power
        } catch (e: IllegalArgumentException) {
            return Result.failure(e)
        }

        power *= base
    }

    return Result.success(total)
}

fun CharArray.toLong(): Result<Long> {
    val base = 10

    var power = 1L
    var total = 0L

    for (i in this.size - 1 downTo 0) {
        try {
            total += this[i].digitToInt(base) * power
        } catch (e: IllegalArgumentException) {
            return Result.failure(e)
        }

        power *= base
    }

    return Result.success(total)
}
