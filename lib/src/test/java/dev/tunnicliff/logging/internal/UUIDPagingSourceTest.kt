// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal

import androidx.paging.PagingSource
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.UUID

class UUIDPagingSourceTest {
    private val loadSize = 20
    private val source = mockk<UUIDPagingSource.Source<MockValue>>()
    private lateinit var pagingSource: UUIDPagingSource<MockValue>

    private val data: List<MockValue> =
        List(120) { index ->
            val endSection = index
                .toString()
                .padStart(length = 12, padChar = '0')
            val idValue = "00000000-0000-0000-0000-$endSection"
            MockValue(UUID.fromString(idValue))
        }

    private val pages: List<List<MockValue>> = data.chunked(loadSize)

    @Before
    fun setup() {
        every { source.getKeyFrom(any()) } answers {
            firstArg<MockValue>().toKey()
        }
        pagingSource = UUIDPagingSource(source)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun loadFirstPage() {
        val expectedResult = pages.first()
        val expectedNextKey = expectedResult.last().toKey()
        coEvery { source.getFirstPage(any()) } returns expectedResult
        val results = runBlocking {
            pagingSource.load(getRefreshParams()).toPage()
        }

        coVerify { source.getFirstPage(loadSize) }
        assertNotNull(results)
        assertNull(results?.prevKey)
        assertEquals(results?.nextKey, expectedNextKey)
        assertEquals(results?.data, expectedResult)
    }

    @Test
    fun loadSecondPage() {
        val expectedResult = pages[1]
        val expectedPreviousKey = pages.first().last().toKey()
        val expectedNextKey = expectedResult.last().toKey()
        coEvery { source.getNextPage(any(), any()) } returns expectedResult
        val results = runBlocking {
            pagingSource.load(getAppendParams(expectedPreviousKey)).toPage()
        }

        coVerify { source.getNextPage(expectedPreviousKey, loadSize) }
        assertNotNull(results)
        assertEquals(results?.prevKey, expectedPreviousKey)
        assertEquals(results?.nextKey, expectedNextKey)
        assertEquals(results?.data, expectedResult)
    }

    private fun getAppendParams(
        key: UUIDPagingSource.Key
    ): PagingSource.LoadParams.Append<UUIDPagingSource.Key> =
        PagingSource.LoadParams.Append(
            key = key,
            loadSize = loadSize,
            placeholdersEnabled = false
        )

    private fun getRefreshParams(): PagingSource.LoadParams.Refresh<UUIDPagingSource.Key> =
        PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = loadSize,
            placeholdersEnabled = false
        )
}

private data class MockValue(
    val uuid: UUID,
    val createdAtTimestamp: Instant = Instant.now()
) {
    fun toKey(): UUIDPagingSource.Key =
        UUIDPagingSource.Key(uuid, createdAtTimestamp)
}

private fun PagingSource.LoadResult<UUIDPagingSource.Key, MockValue>.toPage(): PagingSource.LoadResult.Page<UUIDPagingSource.Key, MockValue>? =
    when (this) {
        is PagingSource.LoadResult.Page -> this
        is PagingSource.LoadResult.Error,
        is PagingSource.LoadResult.Invalid -> {
            fail("Unexpected result $this")
            null
        }
    }
