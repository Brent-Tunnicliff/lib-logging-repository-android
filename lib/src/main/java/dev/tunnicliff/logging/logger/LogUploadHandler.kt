package dev.tunnicliff.logging.logger

import dev.tunnicliff.logging.model.LogLevel

/**
 * Interface for handling upload of logs to external systems.
 */
interface LogUploadHandler {
    suspend fun uploadLog(level: LogLevel, tag: String, message: String, throwable: Throwable?)
}