// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.tunnicliff.logging.R

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

@Composable
fun LogLevel.localisedString(): String =
    when (this) {
        LogLevel.CRITICAL -> R.string.log_level_critical
        LogLevel.DEBUG -> R.string.log_level_debug
        LogLevel.ERROR -> R.string.log_level_error
        LogLevel.INFO -> R.string.log_level_info
        LogLevel.WARNING -> R.string.log_level_warning
    }.let { stringResource(it) }
