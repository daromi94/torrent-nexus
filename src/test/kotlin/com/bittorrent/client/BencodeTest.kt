package com.bittorrent.client

import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BencodeTest {
    @Test
    fun `it should return error if malformed`() {
        // Given
        val raw = "🐛".toByteArray()

        // When
        val maybeDecoded = bdecode(raw)

        // Then
        assertTrue { maybeDecoded.isFailure }
    }

    @Nested
    inner class Integer {
        @Test
        fun `it should return error if open fragment`() {
            // Given
            val raw = "i42".toByteArray()

            // When
            val maybeDecoded = bdecode(raw)

            // Then
            assertTrue { maybeDecoded.isFailure }
        }

        @Test
        fun `it should return error if unparseable integer data`() {
            // Given
            val raw = "i🐛e".toByteArray()

            // When
            val maybeDecoded = bdecode(raw)

            // Then
            assertTrue { maybeDecoded.isFailure }
        }

        @Test
        fun `it should return expected if happy path`() {
            // Given
            val raw = "i42e".toByteArray()

            // When
            val maybeDecoded = bdecode(raw)

            // Then
            assertTrue { maybeDecoded.isSuccess }

            maybeDecoded.onSuccess { assertEquals(it, 42L) }
        }
    }
}
