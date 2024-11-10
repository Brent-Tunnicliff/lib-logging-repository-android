// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.view.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.model.LogLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal abstract class LogsViewModel : ViewModel() {
    abstract val logsState: StateFlow<PagingData<LogEntity>>
    abstract fun viewCreated()
}

internal class DefaultLogsViewModel(
    private val pager: Pager<Int, LogEntity>
) : LogsViewModel() {
    override val logsState: StateFlow<PagingData<LogEntity>>
        get() = _logsState.asStateFlow()

    private var _logsState: MutableStateFlow<PagingData<LogEntity>> =
        MutableStateFlow(value = PagingData.empty())

    override fun viewCreated() {
        viewModelScope.launch {
            pager
                .flow
                .cachedIn(viewModelScope)
                .collect {
                    _logsState.emit(it)
                }
        }
    }
}

internal object PreviewLogsViewModel : LogsViewModel() {
    override val logsState: StateFlow<PagingData<LogEntity>>
        get() = MutableStateFlow(PagingData.from(logs))

    override fun viewCreated() {}

    private val logs = LogLevel.entries.map {
        LogEntity.mock(
            level = it,
            throwable = if (it == LogLevel.ERROR) LogEntity.Throwable.mock() else null,
            uploaded = it == LogLevel.DEBUG
        )
    }
}
