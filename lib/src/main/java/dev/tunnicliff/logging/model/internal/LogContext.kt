// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.model.internal

import com.google.gson.Gson
import dev.tunnicliff.logging.model.LogLevel

internal data class LogContext(
    val level: LogLevel,
    val tag: String,
    val message: String,
    val throwable: Throwable?
) {
    override fun toString(): String {
        val throwableString = throwable?.let {
            Gson().toJson(it)
        } ?: "null"

        return listOf(
            "level: '${level.name}'",
            "tag: '$tag'",
            "message: '$message'",
            "throwable: '$throwableString'"
        ).joinToString()
    }
}
