// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.tunnicliff.logging.demo.view.helper.DefaultDemoOptionsViewModel
import dev.tunnicliff.logging.demo.view.helper.DemoOptionsViewModel
import kotlin.reflect.KClass

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
        when (modelClass) {
            DemoOptionsViewModel::class -> DefaultDemoOptionsViewModel() as T
            else -> throw Exception("Unable to resolve view model of type $modelClass")
        }
}
