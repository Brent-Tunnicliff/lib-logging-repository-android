// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class LocalPersistenceRetentionTests {
    @Test
    fun getTimestampThreeMonths() {
        val retention = LocalPersistenceRetention.ThreeMonths
        val time = Instant.parse("2024-11-12T00:00:00.00Z")
        val expectedResult = Instant.parse("2024-08-13T00:00:00.00Z")
        val result = retention.getTimestampFrom(time)
        assertEquals(expectedResult, result)
    }

    @Suppress("DEPRECATION")
    @Test
    fun getTimestampFiveMinutes() {
        val retention = LocalPersistenceRetention.FiveMinutes
        val time = Instant.parse("2024-11-12T00:10:00.00Z")
        val expectedResult = Instant.parse("2024-11-12T00:05:00.00Z")
        val result = retention.getTimestampFrom(time)
        assertEquals(expectedResult, result)
    }
}