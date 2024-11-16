// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger.internal

import dev.tunnicliff.logging.logger.Logger
import dev.tunnicliff.logging.model.LogLevel
import dev.tunnicliff.logging.model.internal.LogContext

/**
 * Used as a simple backup if we cannot resolve the real logger.
 *
 * Only sends logs to the system log.
 */
internal class BackupLogger : Logger {
    private val systemLog: SystemLog = SystemLogWrapper()

    //region Logger

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        systemLog.log(
            LogContext(
                level = LogLevel.DEBUG,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )
    }

    override fun info(tag: String, message: String, throwable: Throwable?) =
        systemLog.log(
            LogContext(
                level = LogLevel.INFO,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )

    override fun warning(tag: String, message: String, throwable: Throwable?) =
        systemLog.log(
            LogContext(
                level = LogLevel.WARNING,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )

    override fun error(tag: String, message: String, throwable: Throwable?) =
        systemLog.log(
            LogContext(
                level = LogLevel.ERROR,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )

    override fun critical(tag: String, message: String, throwable: Throwable?) =
        systemLog.log(
            LogContext(
                level = LogLevel.CRITICAL,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )

    //endregion
}