package dev.tunnicliff.logging.internal

import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.internal.database.LoggingRepositoryDatabase
import dev.tunnicliff.logging.model.LogLevel
import java.time.Instant

internal interface LogWriter {
    /**
     * Write the log to disk.
     *
     * @return the database id of the log.
     */
    suspend fun writeLog(context: LogContext): Long

    suspend fun setLogAsUploaded(id: Long, uploaded: Boolean)
}

internal class DefaultLogWriter(
    private val database: LoggingRepositoryDatabase,
    // Making `logUploader` a lambda to avoid cycle dependency.
    private val logUploader: () -> LogUploader,
    private val systemLog: SystemLog,
) : LogWriter {
    private companion object {
        const val TAG = "DefaultLogWriter"
    }

    // region LogWriter

    override suspend fun writeLog(context: LogContext): Long {
        try {
            val now = Instant.now()
            return database.logDao().insert(
                LogEntity(
                    // Setting id as `0` makes it auto generate a new id.
                    id = 0,
                    level = context.level,
                    message = context.message,
                    tag = context.tag,
                    throwable = context.throwable,
                    timestampCreated = now,
                    timestampUpdated = now,
                    uploaded = false
                )
            )
        } catch (exception: Exception) {
            logUploadException(
                cause = "writeLog, $context",
                exception = exception
            )
            throw exception
        }
    }

    override suspend fun setLogAsUploaded(id: Long, uploaded: Boolean) {
        try {
            val now = Instant.now()
            val entity = database.logDao().getLog(id)
            entity.uploaded = uploaded
            entity.timestampUpdated = now
            database.logDao().update(entity)
        } catch (exception: Exception) {
            logUploadException(
                cause = "setLogAsUploaded, $id, $uploaded",
                exception = exception
            )
            throw exception
        }
    }

    // endregion

    // region Private functions

    // If uploading throws exception then log in the other methods.
    private suspend fun logUploadException(cause: String, exception: Exception) {
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
