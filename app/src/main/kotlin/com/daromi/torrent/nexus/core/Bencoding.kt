package com.daromi.torrent.nexus.core

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.daromi.torrent.nexus.util.Error
import com.daromi.torrent.nexus.util.b
import com.daromi.torrent.nexus.util.char
import com.daromi.torrent.nexus.util.indexOf
import com.daromi.torrent.nexus.util.toCharArray
import com.daromi.torrent.nexus.util.toInt
import com.daromi.torrent.nexus.util.toLong

fun bendecode(raw: ByteArray): Either<BendecodeError, Any> {
    if (raw.isEmpty()) {
        return IllegalInputError("raw must be non-empty").left()
    }

    return decode(raw).map { it.data }
}

sealed class BendecodeError(
    override val message: String,
) : Error

class IllegalInputError(
    override val message: String,
) : BendecodeError(message)

class IllegalStateError(
    override val message: String,
) : BendecodeError(message)

private data class Chunk(
    val data: Any,
    val pivot: Int,
)

private fun decode(
    raw: ByteArray,
    pivot: Int = 0,
): Either<BendecodeError, Chunk> {
    if (pivot >= raw.size) {
        return IllegalStateError("pivot $pivot must be an index of raw").left()
    }

    val token = raw[pivot]

    return when {
        b(48) <= token && token <= b(57) -> decodeString(raw, pivot)

        token == b(105) -> decodeInteger(raw, pivot)

        else -> IllegalInputError("invalid token '${token.char()}' at index $pivot").left()
    }
}

private fun decodeString(
    raw: ByteArray,
    pivot: Int,
): Either<BendecodeError, Chunk> {
    val colonIndex = raw.indexOf(b(58), pivot + 1)
    if (colonIndex == -1) {
        return IllegalInputError("colon for digit at index $pivot missing").left()
    }

    val length = raw.sliceArray(pivot..<colonIndex).toCharArray().toInt()
    if (length == null) {
        return IllegalInputError("string length at index $pivot cannot be parsed").left()
    }

    val endIndex = colonIndex + length
    if (endIndex >= raw.size) {
        return IllegalInputError("string length at index $pivot exceeds input size").left()
    }

    val data = raw.sliceArray(colonIndex + 1..endIndex).toCharArray().joinToString("")

    return Chunk(data, endIndex).right()
}

private fun decodeInteger(
    raw: ByteArray,
    pivot: Int,
): Either<BendecodeError, Chunk> {
    val eIndex = raw.indexOf(b(101), pivot + 1)
    if (eIndex == -1) {
        return IllegalInputError("matching 'e' for 'i' at index $pivot missing").left()
    }

    val data = raw.sliceArray(pivot + 1..<eIndex).toCharArray().toLong()
    if (data == null) {
        return IllegalInputError("integer data at index $pivot cannot be parsed").left()
    }

    return Chunk(data, eIndex).right()
}
