package dev.tunnicliff.logging.internal

import android.util.Log
import dev.tunnicliff.logging.model.LogLevel

/**
 * Wrapper for the system Log.
 */
internal interface SystemLog {
    fun log(context: LogContext)
}

internal class SystemLogWrapper : SystemLog {
    override fun log(context: LogContext) {
        val isLoggable = Log.isLoggable(context.tag, context.level.toSystemValue())
        if (!isLoggable) {
            return
        }

        with(context) {
            when (level) {
                LogLevel.CRITICAL -> Log.wtf(tag, message, throwable)
                LogLevel.DEBUG -> Log.d(tag, message, throwable)
                LogLevel.ERROR -> Log.e(tag, message, throwable)
                LogLevel.INFO -> Log.i(tag, message, throwable)
                LogLevel.WARNING -> Log.w(tag, message, throwable)
            }
        }
    }
}

private fun LogLevel.toSystemValue(): Int = when (this) {
    LogLevel.CRITICAL -> Log.ASSERT
    LogLevel.DEBUG -> Log.DEBUG
    LogLevel.ERROR -> Log.ERROR
    LogLevel.INFO -> Log.INFO
    LogLevel.WARNING -> Log.WARN
}