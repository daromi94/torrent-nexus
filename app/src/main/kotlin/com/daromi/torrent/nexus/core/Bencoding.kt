package com.daromi.torrent.nexus.core

import com.daromi.torrent.nexus.util.b
import com.daromi.torrent.nexus.util.char
import com.daromi.torrent.nexus.util.indexOf
import com.daromi.torrent.nexus.util.toCharArray
import com.daromi.torrent.nexus.util.toInt
import com.daromi.torrent.nexus.util.toLong

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

    val token = raw[pivot]

    return when {
        b(48) <= token && token <= b(57) -> decodeString(raw, pivot)

        token == b(105) -> decodeInteger(raw, pivot)

        else -> Result.failure(IllegalArgumentException("invalid token '${token.char()}' at index $pivot"))
    }
}

private fun decodeString(
    raw: ByteArray,
    pivot: Int,
): Result<Chunk> {
    val colonIndex = raw.indexOf(b(58), pivot + 1)
    if (colonIndex == -1) {
        return Result.failure(IllegalArgumentException("colon for digit at index $pivot missing"))
    }

    val length = raw.sliceArray(pivot..<colonIndex).toCharArray().toInt()
    if (length == null) {
        return Result.failure(IllegalArgumentException("string length at index $pivot cannot be parsed"))
    }

    val endIndex = colonIndex + length
    if (endIndex >= raw.size) {
        return Result.failure(IndexOutOfBoundsException("string length at index $pivot exceeds input size"))
    }

    val data = raw.sliceArray(colonIndex + 1..endIndex).toCharArray().joinToString("")

    return Result.success(Chunk(data, endIndex))
}

private fun decodeInteger(
    raw: ByteArray,
    pivot: Int,
): Result<Chunk> {
    val eIndex = raw.indexOf(b(101), pivot + 1)
    if (eIndex == -1) {
        return Result.failure(IllegalArgumentException("matching 'e' for 'i' at index $pivot missing"))
    }

    val data = raw.sliceArray(pivot + 1..<eIndex).toCharArray().toLong()
    if (data == null) {
        return Result.failure(IllegalArgumentException("integer data at index $pivot cannot be parsed"))
    }

    return Result.success(Chunk(data, eIndex))
}
