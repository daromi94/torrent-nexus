package com.bittorrent.client

import com.bittorrent.client.shared.byte
import com.bittorrent.client.shared.char
import com.bittorrent.client.shared.indexOf
import com.bittorrent.client.shared.toCharArray
import com.bittorrent.client.shared.toInt
import com.bittorrent.client.shared.toLong

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
        Character.isDigit(token) -> decodeString(raw, pivot)

        token == 'i' -> decodeInteger(raw, pivot)

        else -> Result.failure(IllegalArgumentException("invalid token '$token' at index $pivot"))
    }
}

private fun decodeString(
    raw: ByteArray,
    pivot: Int,
): Result<Chunk> {
    val colonIndex = raw.indexOf(':'.byte(), pivot + 1)

    if (colonIndex == -1) {
        return Result.failure(IllegalArgumentException("colon for digit at index $pivot missing"))
    }

    val maybeLength = raw.sliceArray(pivot..<colonIndex).toCharArray().toInt()

    if (maybeLength.isFailure) {
        return Result.failure(IllegalArgumentException("string length at index $pivot cannot be parsed"))
    }

    val length = maybeLength.getOrThrow()

    val endIndex = colonIndex + length

    if (endIndex >= raw.size) {
        return Result.failure(IllegalArgumentException("string length at index $pivot exceeds input size"))
    }

    val data = raw.sliceArray(colonIndex + 1..endIndex).toCharArray().joinToString("")

    return Result.success(Chunk(data, endIndex))
}

private fun decodeInteger(
    raw: ByteArray,
    pivot: Int,
): Result<Chunk> {
    val eIndex = raw.indexOf('e'.byte(), pivot + 1)

    if (eIndex == -1) {
        return Result.failure(IllegalArgumentException("matching 'e' for 'i' at index $pivot missing"))
    }

    val maybeData = raw.sliceArray(pivot + 1..<eIndex).toCharArray().toLong()

    if (maybeData.isFailure) {
        return Result.failure(IllegalArgumentException("integer data at index $pivot cannot be parsed"))
    }

    val data = maybeData.getOrThrow()

    return Result.success(Chunk(data, eIndex))
}
