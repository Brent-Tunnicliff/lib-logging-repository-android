// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.model

/**
 * Specifies the log level.
 */
enum class LogLevel {
    CRITICAL,
    DEBUG,
    ERROR,
    INFO,
    WARNING,
}

/**
 * Returns the list of all LogLevels supported for the specific minimum LogLevel.
 */
val LogLevel.allowedLogLevels: List<LogLevel>
    get() = when (this) {
        LogLevel.DEBUG -> LogLevel.entries
        LogLevel.INFO -> listOf(LogLevel.INFO, LogLevel.WARNING, LogLevel.ERROR, LogLevel.CRITICAL)
        LogLevel.WARNING -> listOf(LogLevel.WARNING, LogLevel.ERROR, LogLevel.CRITICAL)
        LogLevel.ERROR -> listOf(LogLevel.ERROR, LogLevel.CRITICAL)
        LogLevel.CRITICAL -> listOf(LogLevel.CRITICAL)
    }
