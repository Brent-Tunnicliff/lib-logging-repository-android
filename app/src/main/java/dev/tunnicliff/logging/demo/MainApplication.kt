// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo

import android.app.Application
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.demo.container.AppContainer
import dev.tunnicliff.logging.model.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // We need to initialise the DI container asap.
        AppContainer.initialise(applicationContext)
        applicationScope.launch {
            LoggingContainer.SHARED.loggingConfigurationManager().setMinimumLogLevel(LogLevel.DEBUG)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel()
    }
}