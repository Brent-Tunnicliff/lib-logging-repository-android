package dev.tunnicliff.logger

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

//region Interface

/**
 * Manages recording and sending logs to console.
 *
 * Use `Logger.new()` to get the usable instance.
 */
interface Logger {
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
     * Builder for creating the Logger.
     *
     * There should only be one logger instance for the application.
     *
     * @param context application context.
     */
    class Builder(private val context: Context) {
        private var systemLog = SystemLog.INSTANCE
        private var uploadHandler: UploadHandler? = null

        fun build(): Logger = LoggerImpl(
            context = context,
            isDebug = BuildConfig.DEBUG,
            systemLog = systemLog,
            uploadHandler = uploadHandler
        )

        fun uploadHandler(uploadHandler: UploadHandler): Builder = this.apply {
            this.uploadHandler = uploadHandler
        }
    }
}

//endregion

//region Implementation

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "logger")

private data class LogContext(
    val level: LogLevel,
    val tag: String,
    val message: String,
    val throwable: Throwable?
)

@OptIn(DelicateCoroutinesApi::class)
private class LoggerImpl(
    private val context: Context,
    // Using GlobalScope is not ideal, but wanted to avoid having to make each function `suspend`.
    // Probably not an issue though since ideally Logger's lifecycle should match the app.
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val isDebug: Boolean,
    private val systemLog: SystemLog,
    private val uploadHandler: Logger.UploadHandler?
) : Logger {
    private companion object {
        val UPLOAD_PERMISSION_DEFAULT = LogUploadPermission.NOT_SET
        val UPLOAD_PERMISSION_KEY = stringPreferencesKey("UPLOAD_PERMISSION")
    }

    private var currentUploadPermission = LogUploadPermission.NOT_SET

    init {
        coroutineScope.launch {
            getUploadPermissionFlow()
                .onEach {
                    currentUploadPermission = it
                }
        }
    }

    //region Logger

    override fun getUploadPermission(): LogUploadPermission =
        currentUploadPermission

    override suspend fun getUploadPermissionFlow(): Flow<LogUploadPermission> =
        context.dataStore.data.map { preferences ->
            preferences[UPLOAD_PERMISSION_KEY]?.let {
                LogUploadPermission.valueOf(it)
            } ?: UPLOAD_PERMISSION_DEFAULT
        }

    override fun setUploadPermission(value: LogUploadPermission) {
        coroutineScope.launch {
            context.dataStore.edit {
                it[UPLOAD_PERMISSION_KEY] = value.name
            }
        }
    }

    override fun debug(tag: String, message: String, throwable: Throwable?) =
        log(LogContext(LogLevel.DEBUG, tag, message, throwable))

    override fun info(tag: String, message: String, throwable: Throwable?) =
        log(LogContext(LogLevel.INFO, tag, message, throwable))

    override fun warning(tag: String, message: String, throwable: Throwable?) =
        log(LogContext(LogLevel.WARNING, tag, message, throwable))

    override fun error(tag: String, message: String, throwable: Throwable?) =
        log(LogContext(LogLevel.ERROR, tag, message, throwable))

    override fun critical(tag: String, message: String, throwable: Throwable?) =
        log(LogContext(LogLevel.CRITICAL, tag, message, throwable))

    //endregion

    //region Private functions

    private fun log(context: LogContext) {
        sendLogToSystem(context)
        writeLog(context)
        uploadLog(context)
    }

    private fun sendLogToSystem(context: LogContext) {
        with(context) {
            val isLoggable = systemLog.isLoggable(tag, level) || isDebug
            if (!isLoggable) {
                return
            }

            when (level) {
                LogLevel.CRITICAL -> systemLog.wtf(tag, message, throwable)
                LogLevel.DEBUG -> systemLog.d(tag, message, throwable)
                LogLevel.ERROR -> systemLog.e(tag, message, throwable)
                LogLevel.INFO -> systemLog.i(tag, message, throwable)
                LogLevel.WARNING -> systemLog.w(tag, message, throwable)
            }
        }
    }

    private fun writeLog(context: LogContext) {
        TODO("Implement database")
    }

    private fun uploadLog(context: LogContext) {
        if (uploadHandler == null || !currentUploadPermission.isAllowed) {
            return
        }

        coroutineScope.launch {
            with(context) {
                uploadHandler.uploadLog(level, tag, message, throwable)
            }
        }
    }

    //endregion
}

//endregion