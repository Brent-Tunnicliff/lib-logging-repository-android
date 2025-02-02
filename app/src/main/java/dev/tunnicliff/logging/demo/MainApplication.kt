// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo

import android.app.Application
import dev.tunnicliff.logging.model.LocalPersistenceRetention
import dev.tunnicliff.logging.model.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainApplication : Application() {
    private lateinit var container: AppContainer
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)

        applicationScope.launch {
            with(container.loggingConfigurationManager()) {
                setMinimumLogLevel(LogLevel.DEBUG)
                @Suppress("DEPRECATION")
                deleteOldLogs(LocalPersistenceRetention.FiveMinutes)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel()
    }
}