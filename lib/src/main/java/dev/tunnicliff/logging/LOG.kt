// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging

import dev.tunnicliff.logging.logger.Logger

/**
 * Convenient access to the logger without the container.
 */
val LOG: Logger
    get() = WrapperLogger

/**
 * Logger that wraps the current one in the container.
 *
 * If none are available, then the log is dropped.
 */
private object WrapperLogger : Logger {
    override fun debug(tag: String, message: String, throwable: Throwable?) {
        LoggingContainer.LOGGER?.debug(
            tag = tag,
            message = message,
            throwable = throwable
        )
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        LoggingContainer.LOGGER?.info(
            tag = tag,
            message = message,
            throwable = throwable
        )
    }

    override fun warning(tag: String, message: String, throwable: Throwable?) {
        LoggingContainer.LOGGER?.warning(
            tag = tag,
            message = message,
            throwable = throwable
        )
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        LoggingContainer.LOGGER?.error(
            tag = tag,
            message = message,
            throwable = throwable
        )
    }

    override fun critical(tag: String, message: String, throwable: Throwable?) {
        LoggingContainer.LOGGER?.critical(
            tag = tag,
            message = message,
            throwable = throwable
        )
    }
}
