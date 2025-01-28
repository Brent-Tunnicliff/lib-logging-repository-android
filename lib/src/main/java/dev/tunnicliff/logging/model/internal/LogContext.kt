// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.model.internal

import dev.tunnicliff.logging.BuildConfig
import dev.tunnicliff.logging.internal.database.toEntity
import dev.tunnicliff.logging.model.LogLevel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal data class LogContext(
    val level: LogLevel,
    val tag: String,
    val message: String,
    val packageName: String,
    val throwable: Throwable?
) {
    companion object {
        fun mock(
            level: LogLevel = LogLevel.DEBUG,
            tag: String = "MockLogContext",
            message: String = "This is a message",
            packageName: String = BuildConfig.LIBRARY_PACKAGE_NAME,
            throwable: Throwable? = null
        ): LogContext =
            LogContext(
                level = level,
                tag = tag,
                message = message,
                packageName = packageName,
                throwable = throwable
            )
    }

    override fun toString(): String {
        val throwableString = throwable?.let {
            Json.encodeToString(it.toEntity())
        } ?: "null"

        return listOf(
            "level: '${level.name}'",
            "tag: '$tag'",
            "message: '$message'",
            "packageName: '$packageName'",
            "throwable: '$throwableString'"
        ).joinToString()
    }
}
