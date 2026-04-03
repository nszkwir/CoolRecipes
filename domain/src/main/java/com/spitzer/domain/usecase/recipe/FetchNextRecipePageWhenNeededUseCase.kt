package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.recipe.result.RecipePaginationResult
import javax.inject.Inject

class FetchNextRecipePageWhenNeededUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        elementIndex: Int,
        sortCriteria: RecipeSortCriteria,
        sortOrder: RecipeSortOrder
    ): RecipePaginationResult {
        return repository.fetchRecipeList(
            elementIndex = elementIndex,
            sortCriteria = sortCriteria,
            sortOrder = sortOrder
        )
    }
}
