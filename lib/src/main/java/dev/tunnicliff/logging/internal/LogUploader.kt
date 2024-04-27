package dev.tunnicliff.logging.internal

import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.logging.logger.LogUploadHandler
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import kotlinx.coroutines.flow.first

internal interface LogUploader {
    suspend fun upload(logContext: LogContext): Boolean
}

internal class DefaultLogUploader(
    private val loggingConfigurationManager: LoggingConfigurationManager,
    // Making `logWriter` a lambda to avoid cycle dependency.
    private val logWriter: () -> LogWriter,
    private val systemLog: SystemLog,
    private val uploadHandler: LogUploadHandler?
) : LogUploader {
    private companion object {
        const val TAG = "DefaultLogUploader"
    }

    // region LogUploader

    override suspend fun upload(logContext: LogContext): Boolean {
        val permissionToUpload = loggingConfigurationManager.getUploadPermission().first().isAllowed
        if (uploadHandler == null || !permissionToUpload) {
            return false
        }

        try {
            with(logContext) {
                uploadHandler.uploadLog(
                    level = level,
                    tag = tag,
                    message = message,
                    throwable = throwable
                )
            }

            return true
        } catch (exception: Exception) {
            logUploadException(
                cause = logContext,
                exception = exception
            )

            return false
        }
    }

    // endregion

    // region Private functions

    // If uploading throws exception then log in the other methods.
    private suspend fun logUploadException(cause: LogContext, exception: Exception) {
        val logContext = LogContext(
            level = LogLevel.CRITICAL,
            tag = TAG,
            message = "Uploading log caused unexpected exception thrown, cause: $cause",
            throwable = exception
        )

        systemLog.log(logContext)
        logWriter().writeLog(logContext)
    }

    // endregion
}