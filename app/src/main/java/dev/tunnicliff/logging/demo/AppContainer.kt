// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo

import android.content.Context
import dev.tunnicliff.container.Container
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.logger.LogUploadHandler
import dev.tunnicliff.logging.model.LogLevel

class AppContainer(
    private val applicationContext: Context
) : Container(), LoggingContainer.Dependencies {
    val loggingContainer = LoggingContainer(this)

    // region LoggingContainer.Dependencies

    override fun applicationContext(): Context = applicationContext

    override fun uploadHandler(): LogUploadHandler = resolveSingleton {
        object : LogUploadHandler {
            override suspend fun uploadLog(
                level: LogLevel,
                tag: String,
                message: String,
                throwable: Throwable?
            ): Boolean {
                println("Uploading log, level:$level, tag:$tag, message:$message, throwable:$throwable")
                return true
            }
        }
    }

    // region endregion
}