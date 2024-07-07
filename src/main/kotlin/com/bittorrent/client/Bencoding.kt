package com.bittorrent.client

fun bendecode(raw: ByteArray): Result<Any> {
    if (raw.isEmpty()) {
        return Result.failure(IllegalArgumentException("raw must be non-empty"))
    }

    return decode(raw).map { it.data }
}

private data class Chunk(
    val data: Any,
    val pivot: Int,
)

private fun decode(
    raw: ByteArray,
    pivot: Int = 0,
): Result<Chunk> {
    if (pivot >= raw.size) {
        return Result.failure(IllegalStateException("pivot $pivot must be an index of raw"))
    }

    val token = raw[pivot].char()

    return when {
        token == 'i' -> decodeInteger(raw, pivot)

        else -> Result.failure(IllegalArgumentException("invalid token $token at index $pivot"))
    }
}

private fun decodeInteger(
    raw: ByteArray,
    pivot: Int,
): Result<Chunk> {
    val eIndex = raw.indexOf('e'.byte(), startIndex = pivot + 1)

    if (eIndex == -1) {
        return Result.failure(IllegalArgumentException("matching 'e' for 'i' at index $pivot not found"))
    }

    val maybeData = raw.sliceArray(pivot + 1..<eIndex).toCharArray().toLong()

    if (maybeData.isFailure) {
        return Result.failure(IllegalArgumentException("integer data at index $pivot cannot be parsed"))
    }

    val data = maybeData.getOrThrow()

    return Result.success(Chunk(data, eIndex))
}

private fun Byte.char(): Char = this.toInt().toChar()

private fun Char.byte(): Byte = this.code.toByte()

private fun ByteArray.indexOf(
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

private fun ByteArray.toCharArray(): CharArray = this.map { it.char() }.toCharArray()

private fun CharArray.toLong(): Result<Long> {
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
