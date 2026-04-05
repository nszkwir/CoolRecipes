package com.spitzer.domain.usecase.recipedetails

import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.recipedetails.result.RecipeDetailsResult
import javax.inject.Inject

/**
 * Use case responsible for retrieving the detailed information of a specific recipe by its
 */
class GetRecipeDetailsByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: Long): RecipeDetailsResult {
        return repository.getRecipeDetailsById(id = id)
    }
}
