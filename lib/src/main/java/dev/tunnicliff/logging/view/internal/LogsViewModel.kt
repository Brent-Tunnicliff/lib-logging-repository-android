package dev.tunnicliff.logging.view.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.internal.LogsPagingSource
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.internal.database.LoggingDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class LogsViewModel(
    private val loggingDatabase: LoggingDatabase
) : ViewModel() {
    class Factory(private val container: LoggingContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            container.logsViewModel() as T
    }

    private companion object {
        const val PAGE_SIZE = 20
    }

    private var _logsState: MutableStateFlow<PagingData<LogEntity>> =
        MutableStateFlow(value = PagingData.empty())
    internal val logsState: StateFlow<PagingData<LogEntity>>
        get() = _logsState.asStateFlow()

    fun viewCreated() {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                LogsPagingSource(database = loggingDatabase)
            }
                .flow
                .cachedIn(viewModelScope)
                .collect {
                    _logsState.emit(it)
                }
        }
    }
}
