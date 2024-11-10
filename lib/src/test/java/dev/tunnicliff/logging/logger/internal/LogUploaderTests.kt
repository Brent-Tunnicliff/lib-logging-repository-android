// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger.internal

import dev.tunnicliff.logging.logger.LogUploadHandler
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import dev.tunnicliff.logging.model.LogUploadPermission
import dev.tunnicliff.logging.model.internal.LogContext
import dev.tunnicliff.logging.test.helper.MockException
import dev.tunnicliff.logging.test.helper.MockIOException
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class LogUploaderTests {
    private val loggingConfigurationManager = mockk<LoggingConfigurationManager>()
    private val logWriter = mockk<LogWriter>()
    private val systemLog = mockk<SystemLog>()
    private val uploadHandler = mockk<LogUploadHandler>()
    private lateinit var logUploader: LogUploader

    @Before
    fun setup() {
        logUploader = DefaultLogUploader(
            loggingConfigurationManager = loggingConfigurationManager,
            logWriter = { logWriter },
            systemLog = systemLog,
            uploadHandler = uploadHandler
        )
        coEvery { loggingConfigurationManager.getUploadPermission() } returns MutableStateFlow(
            LogUploadPermission.ALLOWED
        )
        coEvery { uploadHandler.uploadLog(any(), any(), any(), any()) } returns true
        every { systemLog.log(any()) } returns Unit
        coEvery { logWriter.writeLog(any()) } returns UUID.randomUUID()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun permissionNotSet() = runTest {
        coEvery { loggingConfigurationManager.getUploadPermission() } returns MutableStateFlow(
            LogUploadPermission.NOT_SET
        )

        logUploader.upload(LogContext.mock())
        coVerify(exactly = 0) { uploadHandler.uploadLog(any(), any(), any(), any()) }
        verify(exactly = 0) { systemLog.log(any()) }
        coVerify(exactly = 0) { logWriter.writeLog(any()) }
    }

    @Test
    fun permissionRejected() = runTest {
        coEvery { loggingConfigurationManager.getUploadPermission() } returns MutableStateFlow(
            LogUploadPermission.REJECTED
        )

        logUploader.upload(LogContext.mock())
        coVerify(exactly = 0) { uploadHandler.uploadLog(any(), any(), any(), any()) }
        verify(exactly = 0) { systemLog.log(any()) }
        coVerify(exactly = 0) { logWriter.writeLog(any()) }
    }

    @Test
    fun permissionAllowedAndUploadSuccess() = runTest {
        coEvery { loggingConfigurationManager.getUploadPermission() } returns MutableStateFlow(
            LogUploadPermission.ALLOWED
        )

        val expected = LogContext.mock(throwable = MockException())
        val result = logUploader.upload(expected)

        assertTrue(result)
        coVerify {
            uploadHandler.uploadLog(
                level = expected.level,
                tag = expected.tag,
                message = expected.message,
                throwable = expected.throwable
            )
        }

        verify(exactly = 0) { systemLog.log(any()) }
        coVerify(exactly = 0) { logWriter.writeLog(any()) }
    }

    @Test
    fun permissionAllowedAndUploadFailed() = runTest {
        coEvery { loggingConfigurationManager.getUploadPermission() } returns MutableStateFlow(
            LogUploadPermission.ALLOWED
        )
        coEvery { uploadHandler.uploadLog(any(), any(), any(), any()) } returns false

        val expected = LogContext.mock(throwable = MockException())
        val result = logUploader.upload(expected)

        assertFalse(result)
        coVerify {
            uploadHandler.uploadLog(
                level = expected.level,
                tag = expected.tag,
                message = expected.message,
                throwable = expected.throwable
            )
        }

        verify(exactly = 0) { systemLog.log(any()) }
        coVerify(exactly = 0) { logWriter.writeLog(any()) }
    }

    @Test
    fun permissionAllowedAndUploadThrowsIOException() = runTest {
        coEvery { loggingConfigurationManager.getUploadPermission() } returns MutableStateFlow(
            LogUploadPermission.ALLOWED
        )
        coEvery { uploadHandler.uploadLog(any(), any(), any(), any()) } throws MockIOException()

        val expected = LogContext.mock(throwable = MockException())
        val result = logUploader.upload(expected)

        assertFalse(result)
        coVerify {
            uploadHandler.uploadLog(
                level = expected.level,
                tag = expected.tag,
                message = expected.message,
                throwable = expected.throwable
            )
        }

        verify { systemLog.log(any()) }
        coVerify { logWriter.writeLog(any()) }
    }
}