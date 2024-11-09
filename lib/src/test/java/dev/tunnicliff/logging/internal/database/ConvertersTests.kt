// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import dev.tunnicliff.logging.model.LogLevel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.UUID

class ConvertersTests {
    private lateinit var converters: Converters

    @Before
    fun setup() {
        converters = Converters()
    }

    @Test
    fun instant() {
        val initialValue = Instant.EPOCH
        val convertedValue = converters.fromInstant(initialValue)
        val result = converters.toInstant(convertedValue)
        assertEquals(initialValue, result)
    }

    @Test
    fun logLevel() {
        for (initialValue in LogLevel.entries) {
            val convertedValue = converters.fromLogLevel(initialValue)
            val result = converters.toLogLevel(convertedValue)
            assertEquals(initialValue, result)
        }
    }

    @Test
    fun uuid() {
        val initialValue = UUID.randomUUID()
        val convertedValue = converters.fromUUID(initialValue)
        val result = converters.toUUID(convertedValue)
        assertEquals(initialValue, result)
    }
}
