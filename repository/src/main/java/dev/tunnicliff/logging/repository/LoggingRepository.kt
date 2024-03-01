package dev.tunnicliff.logging.repository

import android.content.Context
import dev.tunnicliff.logging.repository.internal.DefaultLoggingRepository
import dev.tunnicliff.logging.repository.internal.SystemLog
import dev.tunnicliff.logging.repository.internal.database.LoggingRepositoryDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow

/**
 * Manages recording and sending logs to console.
 *
 * Use `LoggingRepository.Builder()` to get a usable instance.
 */
interface LoggingRepository {
    fun getUploadPermission(): LogUploadPermission
    suspend fun getUploadPermissionFlow(): Flow<LogUploadPermission>
    fun setUploadPermission(value: LogUploadPermission)

    fun debug(tag: String, message: String, throwable: Throwable? = null)
    fun info(tag: String, message: String, throwable: Throwable? = null)
    fun warning(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
    fun critical(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Interface for handling upload of logs to external systems.
     */
    interface UploadHandler {
        suspend fun uploadLog(level: LogLevel, tag: String, message: String, throwable: Throwable?)
    }

    /**
     * Builder for creating the Default Logging Repository.
     *
     * Recommended to only be one LoggingRepository instance for the application.
     *
     * @param context The context for the logging. This is usually the Application context.
     */
    class Builder(private val context: Context) {
        // Using GlobalScope is not ideal, but wanted to avoid having to make each function `suspend`.
        // Probably not an issue though since ideally LoggingRepository's lifecycle should match the app.
        @OptIn(DelicateCoroutinesApi::class)
        private var coroutineScope: CoroutineScope = GlobalScope
        private var systemLog = SystemLog.INSTANCE
        private var uploadHandler: UploadHandler? = null

        fun build(): LoggingRepository = DefaultLoggingRepository(
            context = context,
            coroutineScope = coroutineScope,
            database = LoggingRepositoryDatabase.getDatabase(context),
            isDebug = BuildConfig.DEBUG,
            systemLog = systemLog,
            uploadHandler = uploadHandler
        )

        /**
         * Defines the coroutine scope for the LoggingRepository to use.
         *
         * Default is GlobalScope.
         */
        fun coroutineScope(coroutineScope: CoroutineScope): Builder = this.apply {
            this.coroutineScope = coroutineScope
        }

        /**
         * Defines an upload handler for the LoggingRepository.
         *
         * Default is null.
         */
        fun uploadHandler(uploadHandler: UploadHandler): Builder = this.apply {
            this.uploadHandler = uploadHandler
        }
    }
}
