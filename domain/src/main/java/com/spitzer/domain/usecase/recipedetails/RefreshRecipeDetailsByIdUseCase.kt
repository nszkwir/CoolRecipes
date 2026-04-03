package com.spitzer.domain.usecase.recipedetails

import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.recipedetails.result.RecipeDetailsResult
import javax.inject.Inject

class RefreshRecipeDetailsByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: Long): RecipeDetailsResult {
        return repository.fetchRecipeDetails(id = id)
    }
}
