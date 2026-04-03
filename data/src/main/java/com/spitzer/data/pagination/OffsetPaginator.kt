package com.spitzer.data.pagination

import com.spitzer.domain.model.pagination.IndexPaginator
import com.spitzer.domain.model.pagination.PaginatedData
import com.spitzer.domain.model.pagination.PaginationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A thread-safe, offset-based [IndexPaginator] implementation.
 *
 * This paginator tracks the current offset and delegates actual data fetching
 * to a provided [fetchPage] function. It is agnostic of the data source
 * (network, database, etc.) and can be reused for any paginated entity.
 *
 * @param Value the type of items being paginated
 * @param pageSize the number of items to request per page
 * @param fetchPage a suspend function that fetches a page given an offset and limit.
 *   This function should return a [PageFetchResult] wrapping the page data or an error.
 * @param onPageLoaded optional callback invoked after each successful page load,
 *   useful for persisting items to a local cache/database.
 */
internal class OffsetPaginator<Value>(
    private val pageSize: Int,
    private val fetchPage: suspend (offset: Int, limit: Int) -> PageFetchResult<Value>,
    private val onPageLoaded: (suspend (items: List<Value>, offset: Int) -> Unit)? = null
) : IndexPaginator<Value> {

    private val mutex = Mutex()
    private var currentOffset = 0

    private val _data = MutableStateFlow(PaginatedData.empty<Value>())
    override val data: StateFlow<PaginatedData<Value>> = _data.asStateFlow()

    override suspend fun loadNextPage(elementIndex: Int): PaginationResult = mutex.withLock {
        if (elementIndex < currentOffset) {
            return PaginationResult.AlreadyLoaded
        }

        return fetchAndProcess()
    }

    override suspend fun refresh(): PaginationResult = mutex.withLock {
        currentOffset = 0
        return fetchAndProcess()
    }

    override suspend fun reset() = mutex.withLock {
        currentOffset = 0
        _data.value = PaginatedData.empty()
    }

    /**
     * Internal method to fetch the next page and update state.
     * Must be called within the [mutex] lock.
     */
    private suspend fun fetchAndProcess(): PaginationResult {
        return when (val result = fetchPage(currentOffset, pageSize)) {
            is PageFetchResult.NoInternet -> PaginationResult.NoInternet
            is PageFetchResult.Error -> PaginationResult.Error
            is PageFetchResult.Success -> {
                val page = result.page
                if (page.items.isEmpty()) {
                    return PaginationResult.Empty
                }

                val totalItems = page.totalItems
                val newItems = page.items
                val newOffset = currentOffset + newItems.size

                val updatedItems: MutableList<Value?> =
                    if (currentOffset == 0) {
                        MutableList(totalItems) { null }
                    } else {
                        _data.value.items.toMutableList()
                    }

                newItems.forEachIndexed { index, item ->
                    val targetIndex = currentOffset + index
                    if (targetIndex < updatedItems.size) {
                        updatedItems[targetIndex] = item
                    }
                }

                currentOffset = newOffset

                onPageLoaded?.invoke(newItems, currentOffset - newItems.size)

                _data.update {
                    PaginatedData(
                        items = updatedItems,
                        totalItems = totalItems,
                        currentOffset = newOffset,
                        hasMore = newOffset < totalItems
                    )
                }

                PaginationResult.Success
            }
        }
    }
}
