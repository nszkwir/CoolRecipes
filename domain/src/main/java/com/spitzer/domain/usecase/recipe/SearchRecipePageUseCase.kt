package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.recipe.result.SearchRecipeResult
import javax.inject.Inject

class SearchRecipePageUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        query: String,
        searchCriteria: RecipeSearchCriteria,
        sortCriteria: RecipeSortCriteria,
        sortOrder: RecipeSortOrder
    ): SearchRecipeResult {
        return repository.searchRecipeList(
            query = query,
            searchCriteria = searchCriteria,
            sortCriteria = sortCriteria,
            sortOrder = sortOrder
        )
    }
}
