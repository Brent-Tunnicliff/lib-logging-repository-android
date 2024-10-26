// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.view.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.tunnicliff.logging.LoggingContainer
import dev.tunnicliff.logging.internal.UUIDPagingSource
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.internal.database.LoggingDatabase
import dev.tunnicliff.logging.model.LogLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal abstract class LogsViewModel : ViewModel() {
    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LoggingContainer.SHARED.logsViewModel() as T
    }

    abstract val logsState: StateFlow<PagingData<LogEntity>>
    abstract fun viewCreated()
}

internal class DefaultLogsViewModel(
    private val loggingDatabase: LoggingDatabase
) : LogsViewModel(), UUIDPagingSource.Source<LogEntity> {
    private companion object {
        const val PAGE_SIZE = 20
    }

    override val logsState: StateFlow<PagingData<LogEntity>>
        get() = _logsState.asStateFlow()

    private var _logsState: MutableStateFlow<PagingData<LogEntity>> =
        MutableStateFlow(value = PagingData.empty())

    override fun viewCreated() {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                UUIDPagingSource(this@DefaultLogsViewModel)
            }
                .flow
                .cachedIn(viewModelScope)
                .collect {
                    _logsState.emit(it)
                }
        }
    }

    // region UUIDPagingSource.Source

    override suspend fun getFirstPage(loadSize: Int): List<LogEntity> =
        loggingDatabase.logDao().getFirstLogs(limit = loadSize)

    override suspend fun getKeyBefore(key: UUIDPagingSource.Key): UUIDPagingSource.Key? =
        loggingDatabase.logDao().getPreviousLogs(
            id = key.uuid,
            limit = 1,
            timestampCreated = key.createdAtTimestamp
        )
            .firstOrNull()
            ?.let {
                getKeyFrom(it)
            }

    override suspend fun getNextPage(key: UUIDPagingSource.Key, loadSize: Int): List<LogEntity> =
        loggingDatabase.logDao().getNextLogs(
            id = key.uuid,
            limit = loadSize,
            timestampCreated = key.createdAtTimestamp
        )

    override suspend fun getPreviousPage(
        key: UUIDPagingSource.Key,
        loadSize: Int
    ): List<LogEntity> =
        loggingDatabase.logDao().getPreviousLogs(
            id = key.uuid,
            limit = loadSize,
            timestampCreated = key.createdAtTimestamp
        )

    override fun getKeyFrom(value: LogEntity): UUIDPagingSource.Key =
        UUIDPagingSource.Key(
            createdAtTimestamp = value.timestampCreated,
            uuid = value.id
        )

    // endregion
}

internal object PreviewLogsViewModel : LogsViewModel() {
    override val logsState: StateFlow<PagingData<LogEntity>>
        get() = MutableStateFlow(PagingData.from(logs))

    override fun viewCreated() {}

    private val logs = LogLevel.entries.map {
        LogEntity.mock(
            level = it,
            throwable = if (it == LogLevel.ERROR) Throwable("Example error") else null,
            uploaded = it == LogLevel.DEBUG
        )
    }
}

