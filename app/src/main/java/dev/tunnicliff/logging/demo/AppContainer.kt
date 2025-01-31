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
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import kotlin.reflect.KClass

class AppContainer(
    applicationContext: Context
) : Container() {
    private val loggingContainer: LoggingContainer = LoggingContainer(applicationContext)

    companion object {
        val VIEW_MODEL_FACTORY = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
                when (modelClass) {
                    DemoOptionsViewModel::class -> DefaultDemoOptionsViewModel() as T
                    else -> throw Exception("Unable to resolve view model of type $modelClass")
                }
        }
    }

    init {
        LOG = loggingContainer.logger(BuildConfig.APPLICATION_ID)
    }

    // region Public

    fun loggingConfigurationManager(): LoggingConfigurationManager =
        loggingContainer.loggingConfigurationManager()

    // endregion
}
