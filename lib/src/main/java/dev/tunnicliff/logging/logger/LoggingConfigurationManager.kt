// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger

import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.logging.model.LogUploadPermission
import kotlinx.coroutines.flow.Flow

/**
 * Manages the logger configurations.
 * All these can be configured at runtime, affecting any future logs.
 */
interface LoggingConfigurationManager {
    suspend fun getMinimumLogLevel(): Flow<LogLevel>
    suspend fun getUploadPermission(): Flow<LogUploadPermission>
    suspend fun setMinimumLogLevel(value: LogLevel)
    suspend fun setUploadPermission(value: LogUploadPermission)
}
