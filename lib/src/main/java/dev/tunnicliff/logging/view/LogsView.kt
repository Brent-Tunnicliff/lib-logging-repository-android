// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.view

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
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

fun NavGraphBuilder.logsView(context: Context) {
    composable(
        route = ROUTE,
        label = context.getString(R.string.log_title)
    ) {
        LogsView()
    }
}

fun NavController.navigateToLogsView() {
    navigate(ROUTE)
}

// endregion

/**
 * Full screen view for logs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogsView(viewModel: LogsViewModel = viewModel(factory = LoggingContainer.VIEW_MODEL_FACTORY)) {
    val pagingItems: LazyPagingItems<LogEntity> = viewModel.logsState.collectAsLazyPagingItems()
    var fabHeight by remember { mutableIntStateOf(0) }
    val fabHeightInDp = with(LocalDensity.current) { fabHeight.toDp() }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.viewCreated()
    }

    Scaffold(
        floatingActionButton = {
            SmallFloatingActionButton(
                modifier = Modifier.onGloballyPositioned {
                    fabHeight = it.size.height
                },
                onClick = {
                    showBottomSheet = true
                },
                shape = CircleShape
            ) {
                Icon(
                    Icons.Outlined.Info,
                    stringResource(R.string.log_floating_action_button_content_description)
                )
            }
        }
    ) { paddingValues ->
        BaseList(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = fabHeightInDp + 16.dp)
        ) {
            items(count = pagingItems.itemCount) { index ->
                LogCardView(logEntity = pagingItems[index]!!)
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = stringResource(R.string.log_disclaimer),
                    textAlign = TextAlign.Center
                )

                Button(onClick = {
                    viewModel.exportLogs()
                }) {
                    Text(text = stringResource(R.string.log_export))
                }
            }
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
    }
}
