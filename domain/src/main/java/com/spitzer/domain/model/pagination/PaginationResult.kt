package com.spitzer.domain.model.pagination

/**
 * Represents the outcome of a pagination operation (load next page, refresh, etc.).
 */
sealed class PaginationResult {
    /** The page was loaded successfully. */
    data object Success : PaginationResult()
    /** No items were returned — the source is empty or exhausted. */
    data object Empty : PaginationResult()
    /** The requested index does not require loading a new page. */
    data object AlreadyLoaded : PaginationResult()
    /** The device has no internet connectivity. */
    data object NoInternet : PaginationResult()
    /** An unknown error occurred. */
    data object Error : PaginationResult()
}
