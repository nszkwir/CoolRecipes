package com.spitzer.data.pagination

/**
 * Represents the result of a page fetch operation from a remote or local source.
 *
 * @param T the type of items returned
 */
sealed class PageFetchResult<out T> {
    data class Success<T>(val page: PageResponse<T>) : PageFetchResult<T>()
    data object NoInternet : PageFetchResult<Nothing>()
    data object Error : PageFetchResult<Nothing>()
}
