package dev.tunnicliff.logging.internal

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.internal.database.LoggingDatabase
import java.time.Instant
import java.util.UUID

internal class LogsPagingSource(
    private val database: LoggingDatabase
) : PagingSource<LogsPagingSource.Key, LogEntity>() {
    private companion object {
        val DEFAULT_INSTANT = Instant.MIN!!
        val DEFAULT_UUID = UUID(0, 0)
    }

    data class Key(
        val uuid: UUID,
        val createdAtTimestamp: Instant
    )

    override fun getRefreshKey(state: PagingState<Key, LogEntity>): Key? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, LogEntity> =
        try {
            val key = params.key
            val logs = database.logDao().getLogsOrderedByNewestFirst(
                lastTimestampCreated = key?.createdAtTimestamp ?: DEFAULT_INSTANT,
                lastID = key?.uuid ?: DEFAULT_UUID,
                limit = params.loadSize
            )

            LoadResult.Page(
                data = logs,
                prevKey = null, // Only paging forward.
                nextKey = logs.lastOrNull()?.let {
                    Key(it.id, it.timestampCreated)
                } ?: key
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
}
