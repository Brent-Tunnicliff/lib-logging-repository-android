// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.view

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.tunnicliff.logging.demo.R
import dev.tunnicliff.ui.component.navigation.composable
import dev.tunnicliff.ui.theme.PreviewerTheme
import dev.tunnicliff.ui.theme.ThemedPreviewer

// region Navigation

private const val ROUTE = "MainView"
const val START_DESTINATION = ROUTE

fun NavGraphBuilder.mainView(
    context: Context
) {
    composable(
        route = ROUTE,
        label = context.getString(R.string.main_label)
    ) {
        MainView()
    }
}

fun NavController.navigateToLogsView() {
    navigate(ROUTE)
}

// endregion

@Composable
fun MainView() {
    Text("Use the nav bar options to see logs.")
}

// region Preview

@Preview
@Composable
private fun PreviewLightTheme() = PreviewContent(PreviewerTheme.LIGHT)

@Preview
@Composable
private fun PreviewDarkTheme() = PreviewContent(PreviewerTheme.DARK)

@Composable
private fun PreviewContent(theme: PreviewerTheme) {
    ThemedPreviewer(theme, enablePreviewScrolling = false) {
        MainView()
    }
}

// endregion