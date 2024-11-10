// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger.internal

import dev.tunnicliff.logging.logger.Logger
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.logging.model.allowedLogLevels
import dev.tunnicliff.logging.model.internal.LogContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class DefaultLogger(
    private val coroutineScope: CoroutineScope,
    private val loggingConfigurationManager: LoggingConfigurationManager,
    private val logUploader: LogUploader,
    private val logWriter: LogWriter,
    private val systemLog: SystemLog
) : Logger {
    //region Logger

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        log(
            LogContext(
                level = LogLevel.DEBUG,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )
    }

    override fun info(tag: String, message: String, throwable: Throwable?) =
        log(
            LogContext(
                level = LogLevel.INFO,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )

    override fun warning(tag: String, message: String, throwable: Throwable?) =
        log(
            LogContext(
                level = LogLevel.WARNING,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )

    override fun error(tag: String, message: String, throwable: Throwable?) =
        log(
            LogContext(
                level = LogLevel.ERROR,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )

    override fun critical(tag: String, message: String, throwable: Throwable?) =
        log(
            LogContext(
                level = LogLevel.CRITICAL,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )

    //endregion

    //region Private functions

    private fun log(context: LogContext) {
        coroutineScope.launch {
            val allowedLogLevels = loggingConfigurationManager.getMinimumLogLevel()
                .first()
                .allowedLogLevels

            if (!allowedLogLevels.contains(context.level)) {
                return@launch
            }

            systemLog.log(context)
            val logId = logWriter.writeLog(context)
            val logDidUpload = logUploader.upload(context)
            if (logDidUpload) {
                logWriter.setLogAsUploaded(id = logId, uploaded = true)
            }
        }
    }

    //endregion
}