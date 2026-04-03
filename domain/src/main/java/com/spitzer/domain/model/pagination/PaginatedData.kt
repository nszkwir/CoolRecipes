package com.spitzer.domain.model.pagination

/**
 * Represents a page of data loaded from a paginated source.
 *
 * @param T the type of items in the page
 * @property items the list of items in the current accumulated state.
 *   Nullable entries represent items that have not been loaded yet (placeholder positions).
 * @property totalItems the total number of items available from the source
 * @property currentOffset the current offset (number of items already loaded)
 * @property hasMore whether more pages are available to load
 */
data class PaginatedData<T>(
    val items: List<T?>,
    val totalItems: Int,
    val currentOffset: Int,
    val hasMore: Boolean
) {
    companion object {
        fun <T> empty(): PaginatedData<T> = PaginatedData(
            items = emptyList(),
            totalItems = 0,
            currentOffset = 0,
            hasMore = false
        )
    }
}
