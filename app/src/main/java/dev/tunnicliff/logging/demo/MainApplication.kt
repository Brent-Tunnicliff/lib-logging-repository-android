// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo

import android.app.Application
import dev.tunnicliff.logging.demo.container.AppContainer

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // We need to initialise the DI container asap.
        AppContainer.initialise(applicationContext)
    }
}