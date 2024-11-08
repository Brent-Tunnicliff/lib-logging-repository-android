// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.view.internal.DefaultLogsViewModel
import dev.tunnicliff.logging.view.internal.LogsViewModel
import kotlin.reflect.KClass

internal object ViewModelFactory : ViewModelProvider.Factory {
    private val resolver: LoggingContainer
        get() = LoggingContainer.SHARED

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
        when (modelClass) {
            LogsViewModel::class -> DefaultLogsViewModel(resolver.loggingPager()) as T
            else -> throw Exception("Unable to resolve view model of type $modelClass")
        }
}