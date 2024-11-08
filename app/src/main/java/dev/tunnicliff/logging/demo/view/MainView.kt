// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.tunnicliff.logging.demo.view.helper.DemoOptionsView
import dev.tunnicliff.logging.view.LogsView

@Composable
fun MainView() {
    Column {
        DemoOptionsView()

        Text(text = "Preview:")

        LogsView()
    }
}