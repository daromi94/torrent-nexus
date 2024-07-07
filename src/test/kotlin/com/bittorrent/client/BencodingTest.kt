package com.bittorrent.client

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
        assertTrue { maybeData.isFailure }
    }

    @Test
    fun `it should return error if malformed`() {
        // Given
        val raw = "🐛".toByteArray()

        // When
        val maybeData = bendecode(raw)

        // Then
        assertTrue { maybeData.isFailure }
    }

    @Nested
    inner class Integer {
        @Test
        fun `it should return error if open fragment`() {
            // Given
            val raw = "i42".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isFailure }
        }

        @Test
        fun `it should return error if unparseable integer data`() {
            // Given
            val raw = "i🐛e".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isFailure }
        }

        @Test
        fun `it should return expected if happy path`() {
            // Given
            val raw = "i42e".toByteArray()

            // When
            val maybeData = bendecode(raw)

            // Then
            assertTrue { maybeData.isSuccess }

            maybeData.onSuccess { assertEquals(it, 42L) }
        }
    }
}
