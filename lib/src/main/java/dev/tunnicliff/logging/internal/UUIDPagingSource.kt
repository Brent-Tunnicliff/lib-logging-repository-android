// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.time.Instant
import java.util.UUID

internal class UUIDPagingSource<Value : Any>(
    private val source: Source<Value>
) : PagingSource<UUIDPagingSource.Key, Value>() {
    data class Key(
        val uuid: UUID,
        val createdAtTimestamp: Instant
    )

    interface Source<Value : Any> {
        suspend fun getFirstPage(loadSize: Int): List<Value>
        suspend fun getKeyBefore(key: Key): Key?
        fun getKeyFrom(value: Value): Key
        suspend fun getNextPage(key: Key, loadSize: Int): List<Value>
        suspend fun getPreviousPage(key: Key, loadSize: Int): List<Value>
    }

    override fun getRefreshKey(state: PagingState<Key, Value>): Key? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value> =
        try {
            when (params) {
                is LoadParams.Append -> loadAppend(params)
                is LoadParams.Prepend -> loadPrepend(params)
                is LoadParams.Refresh -> loadRefresh(params)
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    private suspend fun loadAppend(params: LoadParams.Append<Key>): LoadResult<Key, Value> {
        val key = params.key
        val data = source.getNextPage(key, params.loadSize)
        return LoadResult.Page(
            data = data,
            prevKey = params.key,
            nextKey = data.lastOrNull()?.let {
                source.getKeyFrom(it)
            }
        )
    }

    private suspend fun loadPrepend(params: LoadParams.Prepend<Key>): LoadResult<Key, Value> {
        val key = params.key
        val loadSize = params.loadSize
        val data = source.getPreviousPage(key, loadSize)

        val prevKey = data.firstOrNull()?.let {
            source.getKeyBefore(
                source.getKeyFrom(it)
            )
        }

        return LoadResult.Page(
            data = data,
            prevKey = prevKey,
            nextKey = data.lastOrNull()?.let {
                source.getKeyFrom(it)
            }
        )
    }

    private suspend fun loadRefresh(params: LoadParams.Refresh<Key>): LoadResult<Key, Value> {
        val key = params.key
        val loadSize = params.loadSize
        val data = if (key != null) {
            source.getNextPage(key, loadSize)
        } else {
            source.getFirstPage(loadSize)
        }

        return LoadResult.Page(
            data = data,
            prevKey = params.key,
            nextKey = data.lastOrNull()?.let {
                source.getKeyFrom(it)
            }
        )
    }
}
