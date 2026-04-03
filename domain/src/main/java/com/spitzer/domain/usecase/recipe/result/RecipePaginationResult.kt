package com.spitzer.domain.usecase.recipe.result

sealed class RecipePaginationResult {
    data object Success : RecipePaginationResult()
    data object Empty : RecipePaginationResult()
    data object WrongIndex : RecipePaginationResult()
    data object NoInternet : RecipePaginationResult()
    data object Unknown : RecipePaginationResult()
}