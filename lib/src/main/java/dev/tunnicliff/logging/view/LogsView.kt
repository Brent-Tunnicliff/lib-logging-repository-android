package dev.tunnicliff.logging.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.view.internal.LogsViewModel
import dev.tunnicliff.logging.view.internal.PreviewContainer
import dev.tunnicliff.ui.theme.ThemedPreviewer

@Composable
fun LogsView(container: LoggingContainer) {
    val viewModel: LogsViewModel = viewModel(factory = LogsViewModel.Factory(container))
    val pagingItems: LazyPagingItems<LogEntity> = viewModel.logsState.collectAsLazyPagingItems()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.viewCreated()
    }

    LazyColumn(modifier = Modifier.padding()) {
        items(pagingItems.itemCount) { index ->
            Text(text = pagingItems[index]!!.message)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ThemedPreviewer {
        LogsView(PreviewContainer.loggingContainer(LocalContext.current))
    }
}