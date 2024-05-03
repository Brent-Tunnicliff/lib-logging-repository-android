package dev.tunnicliff.logging.logger

import dev.tunnicliff.logging.model.LogLevel

/**
 * Interface for handling upload of logs to external systems.
 */
interface LogUploadHandler {
    /**
     * Uploads log.
     *
     * @return returns [Boolean] value true if upload was success.
     */
    suspend fun uploadLog(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ): Boolean
}