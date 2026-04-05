package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.recipe.result.RecipePaginationResult
import javax.inject.Inject

/**
 * Use case for refreshing the recipe list with specific sorting options.
 *
 */
class RefreshRecipeListUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        sortCriteria: RecipeSortCriteria,
        sortOrder: RecipeSortOrder
    ): RecipePaginationResult {
        return repository.refreshRecipeList(
            sortCriteria = sortCriteria,
            sortOrder = sortOrder
        )
    }
}
