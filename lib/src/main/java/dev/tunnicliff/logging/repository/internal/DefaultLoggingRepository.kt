package dev.tunnicliff.logging.repository.internal

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.tunnicliff.logging.repository.LogLevel
import dev.tunnicliff.logging.repository.LogUploadPermission
import dev.tunnicliff.logging.repository.LoggingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException

internal class DefaultLoggingRepository(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val isDebug: Boolean,
    private val systemLog: SystemLog,
    private val uploadHandler: LoggingRepository.UploadHandler?
) : LoggingRepository {
    private companion object {
        const val TAG = "DefaultLoggingRepository"
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

    //region LoggingRepository

    override fun getUploadPermission(): LogUploadPermission =
        currentUploadPermission

    override suspend fun getUploadPermissionFlow(): Flow<LogUploadPermission> =
        context.dataStore.data
            .catch {
                // dataStore.data throws an IOException when an error is encountered when reading data
                if (it is IOException) {
                    error(TAG, "getUploadPermissionFlow: Error writing data", it)
                    emit(emptyPreferences())
                } else {
                    error(TAG, "getUploadPermissionFlow: Unexpected exception", it)
                    throw it
                }
            }
            .map { preferences ->
                preferences[UPLOAD_PERMISSION_KEY]?.let {
                    LogUploadPermission.valueOf(it)
                } ?: UPLOAD_PERMISSION_DEFAULT
            }

    override fun setUploadPermission(value: LogUploadPermission) {
        coroutineScope.launch {
            try {
                context.dataStore.edit {
                    it[UPLOAD_PERMISSION_KEY] = value.name
                }
            } catch (exception: IOException) {
                error(TAG, "setUploadPermission: Error reading data", exception)
            } catch (exception: Throwable) {
                error(TAG, "setUploadPermission: Unexpected exception", exception)
                throw exception
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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DefaultLoggingRepository")

private data class LogContext(
    val level: LogLevel,
    val tag: String,
    val message: String,
    val throwable: Throwable?
)