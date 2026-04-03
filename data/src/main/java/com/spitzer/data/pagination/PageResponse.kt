package com.spitzer.data.pagination

/**
 * Represents the raw response from a paginated data source.
 *
 * @param T the type of items in the response
 * @property items the items returned for the current page
 * @property totalItems the total number of items available from the source
 */
data class PageResponse<T>(
    val items: List<T>,
    val totalItems: Int
)
