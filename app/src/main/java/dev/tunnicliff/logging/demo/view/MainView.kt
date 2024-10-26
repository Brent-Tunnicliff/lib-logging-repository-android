// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tunnicliff.logging.demo.container.ViewModelFactory
import dev.tunnicliff.logging.demo.view.helper.DemoOptionsView
import dev.tunnicliff.logging.view.LogsView

@Composable
fun MainView(
    viewModel: MainViewModel = viewModel(factory = ViewModelFactory)
) {
    Column {
        DemoOptionsView()

        Text(text = "Preview:")

        LogsView()
    }
}