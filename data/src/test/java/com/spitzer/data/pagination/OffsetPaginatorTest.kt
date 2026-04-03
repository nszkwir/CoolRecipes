package com.spitzer.data.pagination

import com.spitzer.domain.model.pagination.PaginatedData
import com.spitzer.domain.model.pagination.PaginationResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OffsetPaginatorTest {

    private fun createPaginator(
        pageSize: Int = 3,
        totalItems: Int = 10,
        fetchPage: suspend (Int, Int) -> PageFetchResult<String> = { offset, limit ->
            val items = (offset until minOf(offset + limit, totalItems)).map { "item_$it" }
            PageFetchResult.Success(PageResponse(items = items, totalItems = totalItems))
        },
        onPageLoaded: (suspend (List<String>, Int) -> Unit)? = null
    ): OffsetPaginator<String> {
        return OffsetPaginator(
            pageSize = pageSize,
            fetchPage = fetchPage,
            onPageLoaded = onPageLoaded
        )
    }

    @Test
    fun `initial state is empty`() = runTest {
        val paginator = createPaginator()
        val data = paginator.data.value
        assertEquals(PaginatedData.empty<String>(), data)
    }

    @Test
    fun `loadNextPage loads first page`() = runTest {
        val paginator = createPaginator()
        val result = paginator.loadNextPage(0)

        assertEquals(PaginationResult.Success, result)
        val data = paginator.data.value
        assertEquals(10, data.totalItems)
        assertEquals(3, data.currentOffset)
        assertTrue(data.hasMore)
        assertEquals("item_0", data.items[0])
        assertEquals("item_1", data.items[1])
        assertEquals("item_2", data.items[2])
        assertNull(data.items[3])
    }

    @Test
    fun `loadNextPage returns AlreadyLoaded for already loaded index`() = runTest {
        val paginator = createPaginator()
        paginator.loadNextPage(0)
        val result = paginator.loadNextPage(0)
        assertEquals(PaginationResult.AlreadyLoaded, result)
    }

    @Test
    fun `loadNextPage loads subsequent pages`() = runTest {
        val paginator = createPaginator()
        paginator.loadNextPage(0)
        val result = paginator.loadNextPage(3)

        assertEquals(PaginationResult.Success, result)
        val data = paginator.data.value
        assertEquals(6, data.currentOffset)
        assertEquals("item_3", data.items[3])
        assertEquals("item_4", data.items[4])
        assertEquals("item_5", data.items[5])
    }

    @Test
    fun `refresh resets and loads first page`() = runTest {
        val paginator = createPaginator()
        paginator.loadNextPage(0)
        paginator.loadNextPage(3)

        val result = paginator.refresh()
        assertEquals(PaginationResult.Success, result)
        val data = paginator.data.value
        assertEquals(3, data.currentOffset)
        assertEquals("item_0", data.items[0])
    }

    @Test
    fun `reset clears all state`() = runTest {
        val paginator = createPaginator()
        paginator.loadNextPage(0)
        paginator.reset()

        assertEquals(PaginatedData.empty<String>(), paginator.data.value)
    }

    @Test
    fun `NoInternet error is propagated`() = runTest {
        val paginator = OffsetPaginator<String>(
            pageSize = 3,
            fetchPage = { _, _ -> PageFetchResult.NoInternet }
        )
        val result = paginator.loadNextPage(0)
        assertEquals(PaginationResult.NoInternet, result)
    }

    @Test
    fun `generic error is propagated`() = runTest {
        val paginator = OffsetPaginator<String>(
            pageSize = 3,
            fetchPage = { _, _ -> PageFetchResult.Error }
        )
        val result = paginator.loadNextPage(0)
        assertEquals(PaginationResult.Error, result)
    }

    @Test
    fun `empty result returns Empty`() = runTest {
        val paginator = OffsetPaginator<String>(
            pageSize = 3,
            fetchPage = { _, _ ->
                PageFetchResult.Success(PageResponse(items = emptyList(), totalItems = 0))
            }
        )
        val result = paginator.loadNextPage(0)
        assertEquals(PaginationResult.Empty, result)
    }

    @Test
    fun `onPageLoaded callback is invoked`() = runTest {
        var callbackItems: List<String>? = null
        var callbackOffset: Int? = null
        val paginator = createPaginator(
            onPageLoaded = { items, offset ->
                callbackItems = items
                callbackOffset = offset
            }
        )
        paginator.loadNextPage(0)

        assertEquals(listOf("item_0", "item_1", "item_2"), callbackItems)
        assertEquals(0, callbackOffset)
    }

    @Test
    fun `last page sets hasMore to false`() = runTest {
        val paginator = createPaginator(pageSize = 5, totalItems = 5)
        paginator.loadNextPage(0)

        assertFalse(paginator.data.value.hasMore)
        assertEquals(5, paginator.data.value.currentOffset)
    }
}
