// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import org.junit.Assert.assertEquals
import org.junit.Test

class SystemDatabaseSizeInfoTests {
    @Test
    fun calculateSizeReturnsExpected() {
        val systemDatabaseSizeInfo = SystemDatabaseSizeInfo(
            pageSizeBytes = 3,
            pageCount = 4
        )

        assertEquals(12, systemDatabaseSizeInfo.calculateSize())
    }

    @Test
    fun calculateSizeWithNullValuesReturnsZero() {
        val systemDatabaseSizeInfo = SystemDatabaseSizeInfo(
            pageSizeBytes = null,
            pageCount = null
        )

        assertEquals(0, systemDatabaseSizeInfo.calculateSize())
    }

    @Test
    fun calculateSizeWithNullPageSizeBytesReturnsZero() {
        val systemDatabaseSizeInfo = SystemDatabaseSizeInfo(
            pageSizeBytes = null,
            pageCount = 4
        )

        assertEquals(0, systemDatabaseSizeInfo.calculateSize())
    }

    @Test
    fun calculateSizeWithNullPageCountReturnsZero() {
        val systemDatabaseSizeInfo = SystemDatabaseSizeInfo(
            pageSizeBytes = 3,
            pageCount = null
        )

        assertEquals(0, systemDatabaseSizeInfo.calculateSize())
    }

    @Test
    fun calculateSizeWithZeroPageSizeBytesReturnsZero() {
        val systemDatabaseSizeInfo = SystemDatabaseSizeInfo(
            pageSizeBytes = 0,
            pageCount = 4
        )

        assertEquals(0, systemDatabaseSizeInfo.calculateSize())
    }

    @Test
    fun calculateSizeWithZeroPageCountReturnsZero() {
        val systemDatabaseSizeInfo = SystemDatabaseSizeInfo(
            pageSizeBytes = 3,
            pageCount = 0
        )

        assertEquals(0, systemDatabaseSizeInfo.calculateSize())
    }
}