// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger.internal

import dev.tunnicliff.logging.logger.Logger
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.logging.model.allowedLogLevels
import dev.tunnicliff.logging.model.internal.LogContext
import dev.tunnicliff.logging.test.helper.MockException
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultLoggerTests {
    private val logId = UUID.randomUUID()
    private val loggingConfigurationManager = mockk<LoggingConfigurationManager>()
    private val logUploader = mockk<LogUploader>()
    private val logWriter = mockk<LogWriter>()
    private val systemLog = mockk<SystemLog>()

    @Before
    fun setup() {
        every { systemLog.log(any()) } returns Unit
        coEvery { logWriter.writeLog(any()) } returns logId
        coEvery { logWriter.setLogAsUploaded(any(), any()) } returns Unit
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun logAllowed() = runTest {
        val logger = logger(this)

        for (logLevel in LogLevel.entries) {
            // reset test between loops.
            tearDown()
            setup()

            coEvery {
                loggingConfigurationManager.getMinimumLogLevel()
            } returns MutableStateFlow(LogLevel.DEBUG)
            coEvery { logUploader.upload(any()) } returns false

            val tag = "tag"
            val message = "message"
            val throwable = MockException()
            val expected = LogContext(
                level = logLevel,
                message = message,
                tag = tag,
                throwable = throwable
            )

            // When logging
            when (logLevel) {
                LogLevel.CRITICAL -> logger.critical(tag, message, throwable)
                LogLevel.DEBUG -> logger.debug(tag, message, throwable)
                LogLevel.ERROR -> logger.error(tag, message, throwable)
                LogLevel.INFO -> logger.info(tag, message, throwable)
                LogLevel.WARNING -> logger.warning(tag, message, throwable)
            }

            // then we expect the various sub-systems to be passed the correct data.
            advanceTimeBy(1000)
            verify { systemLog.log(expected) }
            coVerify { logWriter.writeLog(expected) }
            coVerify { logUploader.upload(expected) }
        }
    }

    @Test
    fun logNotAllowed() = runTest {
        val logger = logger(this)

        for (minimumLogLevel in LogLevel.entries) {
            val expectedAllowedLogLevels = minimumLogLevel.allowedLogLevels
            val deniedLogLevels = LogLevel.entries.filter { !expectedAllowedLogLevels.contains(it) }

            for (logLevel in deniedLogLevels) {
                // reset test between loops.
                tearDown()
                setup()

                coEvery {
                    loggingConfigurationManager.getMinimumLogLevel()
                } returns MutableStateFlow(minimumLogLevel)
                coEvery { logUploader.upload(any()) } returns false

                // When logging
                when (logLevel) {
                    LogLevel.CRITICAL -> logger.critical("tag", "message")
                    LogLevel.DEBUG -> logger.debug("tag", "message")
                    LogLevel.ERROR -> logger.error("tag", "message")
                    LogLevel.INFO -> logger.info("tag", "message")
                    LogLevel.WARNING -> logger.warning("tag", "message")
                }

                // then we expect none of the sub-systems to be triggered.
                advanceTimeBy(1000)
                verify(exactly = 0) { systemLog.log(any()) }
                coVerify(exactly = 0) { logWriter.writeLog(any()) }
                coVerify(exactly = 0) { logUploader.upload(any()) }
            }
        }
    }

    @Test
    fun logNotMarkedAsUploaded() = runTest {
        val logger = logger(this)
        coEvery {
            loggingConfigurationManager.getMinimumLogLevel()
        } returns MutableStateFlow(LogLevel.DEBUG)
        coEvery { logUploader.upload(any()) } returns false
        logger.critical("tag", "message")
        advanceTimeBy(1000)
        coVerify(exactly = 0) { logWriter.setLogAsUploaded(any(), any()) }
    }

    @Test
    fun uploadLogThenMarkedAsUploaded() = runTest {
        val logger = logger(this)
        coEvery {
            loggingConfigurationManager.getMinimumLogLevel()
        } returns MutableStateFlow(LogLevel.DEBUG)
        coEvery { logUploader.upload(any()) } returns true
        logger.critical("tag", "message")
        advanceTimeBy(1000)
        coVerify { logWriter.setLogAsUploaded(logId, true) }
    }

    private fun logger(coroutineScope: TestScope): Logger =
        DefaultLogger(
            coroutineScope = coroutineScope,
            loggingConfigurationManager = loggingConfigurationManager,
            logUploader = logUploader,
            logWriter = logWriter,
            systemLog = systemLog
        )
}