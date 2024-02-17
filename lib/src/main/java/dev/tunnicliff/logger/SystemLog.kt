package dev.tunnicliff.logger

import android.util.Log

/**
 * Wrapper for the system Log.
 */
internal interface SystemLog {
    companion object {
        val INSTANCE: SystemLog
            get() = SystemLogImpl()
    }

    fun isLoggable(tag: String, level: LogLevel): Boolean

    fun d(tag: String, msg: String, tr: Throwable?)
    fun e(tag: String, msg: String, tr: Throwable?)
    fun i(tag: String, msg: String, tr: Throwable?)
    fun w(tag: String, msg: String, tr: Throwable?)
    fun wtf(tag: String, msg: String, tr: Throwable?)
}

private class SystemLogImpl : SystemLog {
    override fun isLoggable(tag: String, level: LogLevel): Boolean =
        Log.isLoggable(tag, level.toSystemValue())

    override fun d(tag: String, msg: String, tr: Throwable?) {
        Log.d(tag, msg, tr)
    }

    override fun e(tag: String, msg: String, tr: Throwable?) {
        Log.e(tag, msg, tr)
    }

    override fun i(tag: String, msg: String, tr: Throwable?) {
        Log.i(tag, msg, tr)
    }

    override fun w(tag: String, msg: String, tr: Throwable?) {
        Log.w(tag, msg, tr)
    }

    override fun wtf(tag: String, msg: String, tr: Throwable?) {
        Log.wtf(tag, msg, tr)
    }
}

private fun LogLevel.toSystemValue(): Int = when (this) {
    LogLevel.CRITICAL -> Log.ASSERT
    LogLevel.DEBUG -> Log.DEBUG
    LogLevel.ERROR -> Log.ERROR
    LogLevel.INFO -> Log.INFO
    LogLevel.WARNING -> Log.WARN
}