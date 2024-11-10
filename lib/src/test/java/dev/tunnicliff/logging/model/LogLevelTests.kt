// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.model

import org.junit.Assert.assertEquals
import org.junit.Test

class LogLevelTests {
    @Test
    fun debugAllowedLogLevels() {
        // we expect all.
        val expectedResult = LogLevel.entries.sorted()
        val results = LogLevel.DEBUG.allowedLogLevels.sorted()
        assertEquals(expectedResult, results)
    }

    @Test
    fun infoAllowedLogLevels() {
        // we expect all but debug.
        val expectedResult = LogLevel.entries.filter { it != LogLevel.DEBUG }.sorted()
        val results = LogLevel.INFO.allowedLogLevels.sorted()
        assertEquals(expectedResult, results)
    }

    @Test
    fun warningAllowedLogLevels() {
        // we expect only critical, error and warning.
        val expectedResult = listOf(LogLevel.CRITICAL, LogLevel.ERROR, LogLevel.WARNING).sorted()
        val results = LogLevel.WARNING.allowedLogLevels.sorted()
        assertEquals(expectedResult, results)
    }

    @Test
    fun errorAllowedLogLevels() {
        // we expect only critical and error.
        val expectedResult = listOf(LogLevel.CRITICAL, LogLevel.ERROR).sorted()
        val results = LogLevel.ERROR.allowedLogLevels.sorted()
        assertEquals(expectedResult, results)
    }

    @Test
    fun criticalAllowedLogLevels() {
        // we expect only critical.
        val expectedResult = listOf(LogLevel.CRITICAL).sorted()
        val results = LogLevel.CRITICAL.allowedLogLevels.sorted()
        assertEquals(expectedResult, results)
    }
}