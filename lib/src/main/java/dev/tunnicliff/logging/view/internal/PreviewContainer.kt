// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.view.internal

import android.content.Context
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.logger.LogUploadHandler
import dev.tunnicliff.logging.model.LogLevel

internal object PreviewContainer {
    fun loggingContainer(context: Context) = LoggingContainer(
        object : LoggingContainer.Dependencies {
            override fun applicationContext(): Context = context

            override fun uploadHandler(): LogUploadHandler = object : LogUploadHandler {
                override suspend fun uploadLog(
                    level: LogLevel,
                    tag: String,
                    message: String,
                    throwable: Throwable?
                ): Boolean = true
            }
        }
    )
}