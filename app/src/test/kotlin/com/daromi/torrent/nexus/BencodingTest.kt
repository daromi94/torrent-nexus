package com.daromi.torrent.nexus

import com.daromi.torrent.nexus.core.bendecode
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BencodingTest {
    @Test
    fun `it should return error if empty`() {
        // Given
        val raw = ByteArray(0)

        // When
        val maybeData = bendecode(raw)

        // Then
        assertTrue { maybeData.isLeft() }
    }

    @Test
    fun `it should return error if malformed`() {
        // Given
        val raw = "🐛".toByteArray()

        // When
        val maybeData = bendecode(raw)

        // Then
        assertTrue { maybeData.isLeft() }
    }

    @Nested
    inner class String {
        @Test
        fun `it should return error if missing colon`() {
            // Given
            val raw = "5hello".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isLeft() }
        }

        @Test
        fun `it should return error if unparseable length`() {
            // Given
            val raw = "5🐛:hello".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isLeft() }
        }

        @Test
        fun `it should return error if length exceeds input size`() {
            // Given
            val raw = "10:hello".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isLeft() }
        }

        @Test
        fun `it should return expected if zero length`() {
            // Given
            val raw = "0:".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isRight() }

            maybeData.onRight { assertEquals(it, "") }
        }

        @Test
        fun `it should return expected if non-zero length`() {
            // Given
            val raw = "5:hello".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isRight() }
            maybeData.onRight { assertEquals(it, "hello") }
        }
    }

    @Nested
    inner class Integer {
        @Test
        fun `it should return error if missing e`() {
            // Given
            val raw = "i42".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isLeft() }
        }

        @Test
        fun `it should return error if unparseable data`() {
            // Given
            val raw = "i🐛e".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isLeft() }
        }

        @Test
        fun `it should return expected if positive number`() {
            // Given
            val raw = "i42e".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isRight() }
            maybeData.onRight { assertEquals(it, 42L) }
        }
    }
}
