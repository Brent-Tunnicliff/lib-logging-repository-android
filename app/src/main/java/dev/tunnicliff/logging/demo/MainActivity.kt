package dev.tunnicliff.logging.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.tunnicliff.logging.demo.view.MainView
import dev.tunnicliff.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loggingContainer = (application as MainApplication).container.loggingContainer

        setContent {
            AppTheme {
                MainView(loggingContainer)
            }
        }
    }
}
