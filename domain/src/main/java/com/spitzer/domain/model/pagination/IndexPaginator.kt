package com.spitzer.domain.model.pagination

import kotlinx.coroutines.flow.StateFlow

/**
 * A generic contract for paginated data loading.
 *
 * The Paginator is responsible for managing offset/cursor state, thread safety,
 * and exposing a reactive stream of accumulated paginated data.
 *
 * @param Value the type of items being paginated
 */
interface IndexPaginator<Value> {

    /**
     * A reactive stream of the current paginated data state.
     * Observers receive updates whenever a new page is loaded or a refresh occurs.
     */
    val data: StateFlow<PaginatedData<Value>>

    /**
     * Loads the next page if the given [elementIndex] has reached or exceeded
     * the current loaded boundary.
     *
     * @param elementIndex the index of the item that triggered the load
     * @return [PaginationResult] indicating the outcome
     */
    suspend fun loadNextPage(elementIndex: Int): PaginationResult

    /**
     * Resets pagination state and loads the first page from scratch.
     *
     * @return [PaginationResult] indicating the outcome
     */
    suspend fun refresh(): PaginationResult

    /**
     * Resets pagination state without loading data.
     * Useful when the underlying data source or query changes.
     */
    suspend fun reset()
}
