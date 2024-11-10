// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger.internal

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

    suspend fun setLogAsUploaded(id: UUID, uploaded: Boolean)

    /**
     * Deletes logs older than the input.
     *
     * @return Number of logs deleted.
     */
    suspend fun deleteLogsOlderThan(timestamp: Instant): Int
}

internal class DefaultLogWriter(
    private val database: LoggingDatabase,
    // Making `logUploader` a lambda to avoid cycle dependency.
    private val logUploader: () -> LogUploader,
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
                    tag = context.tag,
                    throwable = context.throwable?.toEntity(),
                    timestampCreated = now,
                    timestampUpdated = now,
                    uploaded = false
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

    override suspend fun setLogAsUploaded(id: UUID, uploaded: Boolean) {
        try {
            val now = Instant.now()
            val entity = database.logDao().getLog(id)
            entity.uploaded = uploaded
            entity.timestampUpdated = now
            database.logDao().update(entity)
        } catch (exception: IOException) {
            logException(
                cause = "setLogAsUploaded, $id, $uploaded",
                exception = exception
            )
            throw exception
        }
    }

    override suspend fun deleteLogsOlderThan(timestamp: Instant): Int =
        try {
            database.logDao().deleteLogsOlderThan(timestamp)
        } catch (exception: IOException) {
            logException(
                cause = "deleteLogsOlderThan $timestamp",
                exception = exception
            )
            throw exception
        }

    // endregion

    // region Private functions

    // If uploading throws exception then log in the other methods.
    private suspend fun logException(cause: String, exception: Exception) {
        val logContext = LogContext(
            level = LogLevel.CRITICAL,
            tag = TAG,
            message = "Writing log caused unexpected exception thrown, cause: $cause",
            throwable = exception
        )

        systemLog.log(logContext)
        logUploader().upload(logContext)
    }

    // endregion
}
