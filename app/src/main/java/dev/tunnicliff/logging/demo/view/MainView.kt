// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.demo.PreviewContainer
import dev.tunnicliff.logging.demo.view.helper.DemoOptionsView
import dev.tunnicliff.ui.theme.ThemedPreviewer

@Composable
fun MainView(
    loggingContainer: LoggingContainer,
    viewModel: MainViewModel = viewModel()
) {
    Column {
        DemoOptionsView()

//        LogsView(loggingContainer)
    }
}

@Preview
@Composable
private fun Preview() {
    ThemedPreviewer {
        MainView(PreviewContainer.loggingContainer(LocalContext.current))
    }
}