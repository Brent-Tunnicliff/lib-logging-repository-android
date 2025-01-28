// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.view

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.R
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.view.internal.LogCardView
import dev.tunnicliff.logging.view.internal.LogsViewModel
import dev.tunnicliff.logging.view.internal.PreviewLogsViewModel
import dev.tunnicliff.ui.component.list.BaseList
import dev.tunnicliff.ui.component.navigation.composable
import dev.tunnicliff.ui.theme.PreviewerTheme
import dev.tunnicliff.ui.theme.ThemedPreviewer

// region Navigation

private const val ROUTE = "LogsView"

fun NavGraphBuilder.logsView(
    container: LoggingContainer,
    context: Context
) {
    composable(
        route = ROUTE,
        label = context.getString(R.string.log_title)
    ) {
        LogsView(container)
    }
}

fun NavController.navigateToLogsView() {
    navigate(ROUTE)
}

// endregion

/**
 * Full screen view for logs.
 */
@Composable
fun LogsView(container: LoggingContainer) =
    LogsView(viewModel(factory = container.viewModelFactory()))

@Composable
private fun LogsView(viewModel: LogsViewModel) {
    val pagingItems: LazyPagingItems<LogEntity> = viewModel.logsState.collectAsLazyPagingItems()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.viewCreated()
    }

    BaseList {
        item {
            Text(
                text = stringResource(R.string.log_disclaimer),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
            )
        }

        items(count = pagingItems.itemCount) { index ->
            LogCardView(logEntity = pagingItems[index]!!)
        }
    }
}

@Preview
@Composable
private fun PreviewLightTheme() = PreviewContent(PreviewerTheme.LIGHT)

@Preview
@Composable
private fun PreviewDarkTheme() = PreviewContent(PreviewerTheme.DARK)

@Composable
private fun PreviewContent(theme: PreviewerTheme) {
    ThemedPreviewer(theme, enablePreviewScrolling = false) {
        Scaffold { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                LogsView(viewModel = PreviewLogsViewModel)
            }
        }
//        LogsView(viewModel = PreviewLogsViewModel)
    }
}
