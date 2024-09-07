// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo

import android.app.Application
import dev.tunnicliff.logging.logger.Logger

class MainApplication : Application() {
    companion object {
        private lateinit var _LOGGER: Logger

        val LOGGER: Logger
            get() = _LOGGER
    }

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
        _LOGGER = container.loggingContainer.logger()
    }
}