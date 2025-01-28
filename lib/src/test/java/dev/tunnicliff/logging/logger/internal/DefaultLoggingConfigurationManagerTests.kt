// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger.internal

import android.content.Context
import dev.tunnicliff.logging.internal.database.LogDao
import dev.tunnicliff.logging.internal.database.LoggingDatabase
import dev.tunnicliff.logging.internal.database.SystemDatabaseSizeInfo
import dev.tunnicliff.logging.logger.Logger
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import dev.tunnicliff.logging.model.LocalPersistenceRetention
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class DefaultLoggingConfigurationManagerTests {
    private val context = mockk<Context>()
    private val logger = mockk<Logger>()
    private val database = mockk<LoggingDatabase>()
    private val logDao = mockk<LogDao>()
    private val retention = mockk<LocalPersistenceRetention>()
    private val timestamp = Instant.EPOCH
    private lateinit var loggingConfigurationManager: LoggingConfigurationManager

    @Before
    fun setup() {
        every { database.logDao() } returns logDao
        mockkObject(Logger)
        every { Logger.LOGGING } returns logger
        every { logger.debug(any(), any(), any()) } returns Unit
        every { logger.info(any(), any(), any()) } returns Unit
        every { retention.getTimestampFrom(any()) } returns timestamp

        loggingConfigurationManager = DefaultLoggingConfigurationManager(
            context = context,
            database = database
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun deleteOldLogsReturnsExpectedValue() = runTest {
        val expectedResult = 20
        coEvery { logDao.deleteLogsOlderThan(timestamp) } returns expectedResult
        coEvery { logDao.getDatabaseSizeInfo() } returns emptyList()
        val result = loggingConfigurationManager.deleteOldLogs(retention)
        assertEquals(expectedResult, result)
    }

    // A unit test for a log is weird.
    // But this log will be an important way to gauge how big these databases can get.
    // That is why I have the unit test to make sure it is working as expected.
    @Test
    fun deleteOldLogsSendsLogWithDatabaseSize() = runTest {
        coEvery { logDao.deleteLogsOlderThan(any()) } returns 0
        val sizeInfo = listOf(
            SystemDatabaseSizeInfo(), // 0
            SystemDatabaseSizeInfo(126, 34), // 4284
            SystemDatabaseSizeInfo(245, 10) // 2450
        )
        coEvery { logDao.getDatabaseSizeInfo() } returns sizeInfo
        loggingConfigurationManager.deleteOldLogs(retention)
        verify { logger.info(tag = any(), message = "Logs database size: 6 KB") }
    }
}