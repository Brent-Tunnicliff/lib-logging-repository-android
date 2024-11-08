// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.container

import android.content.Context
import dev.tunnicliff.container.Container
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.logger.LogUploadHandler
import dev.tunnicliff.logging.model.LogLevel

class AppContainer private constructor(
    private val applicationContext: Context
) : Container() {
    companion object {
        private lateinit var _SHARED: AppContainer

        val SHARED: AppContainer
            get() = _SHARED

        /**
         * Initialises the container.
         *
         * After which `SHARED` will be safe to use.
         */
        fun initialise(applicationContext: Context) {
            _SHARED = AppContainer(applicationContext)
            LoggingContainer.initialise(
                object : LoggingContainer.Dependencies {
                    override fun applicationContext(): Context = applicationContext
                    override fun uploadHandler(): LogUploadHandler = _SHARED.uploadHandler()
                }
            )
        }
    }

    // region Private

    private fun uploadHandler(): LogUploadHandler = resolveSingleton {
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