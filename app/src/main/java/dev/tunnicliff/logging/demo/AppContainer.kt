// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.tunnicliff.container.Container
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.demo.view.helper.DefaultDemoOptionsViewModel
import dev.tunnicliff.logging.demo.view.helper.DemoOptionsViewModel
import dev.tunnicliff.logging.logger.LogUploadHandler
import dev.tunnicliff.logging.logger.Logger
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import dev.tunnicliff.logging.model.LogLevel
import kotlin.reflect.KClass

class AppContainer(
    dependencies: Dependencies
) : Container() {
    // region Types

    interface Dependencies {
        fun applicationContext(): Context
    }

    object ViewModelFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
            when (modelClass) {
                DemoOptionsViewModel::class -> DefaultDemoOptionsViewModel() as T
                else -> throw Exception("Unable to resolve view model of type $modelClass")
            }
    }

    companion object {
        private lateinit var CONTAINER: AppContainer
        val LOGGER: Logger
            get() = CONTAINER.loggingContainer.logger()
    }

    // endregion

    private val loggingContainer: LoggingContainer = LoggingContainer(
        object : LoggingContainer.Dependencies {
            override fun applicationContext(): Context =
                dependencies.applicationContext()

            override fun uploadHandler(): LogUploadHandler =
                this@AppContainer.uploadHandler()
        }
    )

    init {
        CONTAINER = this
    }

    // region Internal

    fun loggingConfigurationManager(): LoggingConfigurationManager =
        loggingContainer.loggingConfigurationManager()

    // endregion

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

    // endregion
}