package dev.tunnicliff.logging.internal

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.logging.model.LogUploadPermission
import dev.tunnicliff.logging.logger.Logger
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

internal class DefaultLoggingConfigurationManager(
    private val context: Context,
    // Making `logger` a lambda to avoid cycle dependency.
    private val logger: () -> Logger
) : LoggingConfigurationManager {
    private companion object {
        const val TAG = "DefaultLoggingConfigurationManager"
    }

    // region LoggingPermissionManager

    override suspend fun getMinimumLogLevel(): Flow<LogLevel> =
        getPreference(Preference.MinimumLogLevel) {
            LogLevel.valueOf(it)
        }

    override suspend fun getUploadPermission(): Flow<LogUploadPermission> =
        getPreference(Preference.UploadPermission) {
            LogUploadPermission.valueOf(it)
        }

    override suspend fun setMinimumLogLevel(value: LogLevel) =
        setPreference(Preference.MinimumLogLevel, value.name)

    override suspend fun setUploadPermission(value: LogUploadPermission) =
        setPreference(Preference.UploadPermission, value.name)

    // endregion

    // region Private functions

    private fun <StoredValue, Value> getPreference(
        preference: Preference<StoredValue, Value>,
        mapValue: (StoredValue) -> Value
    ): Flow<Value> =
        context.dataStore.data
            .catch {
                // dataStore.data throws an IOException when an error is encountered when reading data
                if (it is IOException) {
                    logger().critical(TAG, "Reading preference threw IOException", it)
                    // Ignore IOException.
                    emit(emptyPreferences())
                } else {
                    logger().critical(TAG, "Reading preference threw throwable", it)
                    throw it
                }
            }
            .map { preferences ->
                preferences[preference.key]?.let { value ->
                    mapValue(value)
                } ?: preference.defaultValue
            }

    private suspend fun <StoredValue, Value> setPreference(
        preference: Preference<StoredValue, Value>,
        value: StoredValue
    ) {
        try {
            context.dataStore.edit {
                it[preference.key] = value
            }
        } catch (exception: IOException) {
            logger().critical(TAG, "Writing preference threw IOException", exception)
            // Ignore IOException.
        } catch (throwable: Throwable) {
            logger().critical(TAG, "Writing preference threw throwable", throwable)
            throw throwable
        }
    }

    // endregion
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DefaultLoggingRepository")

private sealed class Preference<StoredValue, DefaultValue>(
    val key: Preferences.Key<StoredValue>,
    val defaultValue: DefaultValue
) {
    data object MinimumLogLevel : Preference<String, LogLevel>(
        stringPreferencesKey("MINIMUM_LOG_LEVEL"),
        LogLevel.INFO
    )

    data object UploadPermission : Preference<String, LogUploadPermission>(
        stringPreferencesKey("UPLOAD_PERMISSION"),
        LogUploadPermission.NOT_SET
    )
}