package com.spitzer.domain.usecase.recipedetails

import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.recipedetails.result.RecipeDetailsResult
import javax.inject.Inject

/**
 * Use case for refreshing the details of a specific recipe using its unique identifier.
 */
class RefreshRecipeDetailsByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: Long): RecipeDetailsResult {
        return repository.fetchRecipeDetails(id = id)
    }
}
