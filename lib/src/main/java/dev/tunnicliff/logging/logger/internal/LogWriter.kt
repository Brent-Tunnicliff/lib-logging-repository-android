// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger.internal

import dev.tunnicliff.logging.BuildConfig
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.internal.database.LoggingDatabase
import dev.tunnicliff.logging.internal.database.toEntity
import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.logging.model.internal.LogContext
import java.io.IOException
import java.time.Instant
import java.util.UUID

internal interface LogWriter {
    /**
     * Write the log to disk.
     *
     * @return the database id of the log.
     */
    suspend fun writeLog(context: LogContext): UUID
}

internal class DefaultLogWriter(
    private val database: LoggingDatabase,
    private val systemLog: SystemLog,
) : LogWriter {
    private companion object {
        const val TAG = "DefaultLogWriter"
    }

    // region LogWriter

    override suspend fun writeLog(context: LogContext): UUID {
        try {
            val now = Instant.now()
            val id = UUID.randomUUID()
            database.logDao().insert(
                LogEntity(
                    id = id,
                    level = context.level,
                    message = context.message,
                    packageName = context.packageName,
                    tag = context.tag,
                    throwable = context.throwable?.toEntity(),
                    timestampCreated = now
                )
            )

            return id
        } catch (exception: IOException) {
            logException(
                cause = "writeLog, $context",
                exception = exception
            )
            throw exception
        }
    }

    // endregion

    // region Private functions

    // If uploading throws exception then log in the other methods.
    private fun logException(cause: String, exception: Exception) {
        val logContext = LogContext(
            level = LogLevel.CRITICAL,
            tag = TAG,
            message = "Writing log caused unexpected exception thrown, cause: $cause",
            packageName = BuildConfig.LIBRARY_PACKAGE_NAME,
            throwable = exception
        )

        systemLog.log(logContext)
    }

    // endregion
}
