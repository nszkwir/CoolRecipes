package com.spitzer.domain.usecase.recipedetails.result

import com.spitzer.domain.model.recipe.RecipeDetails

sealed class RecipeDetailsResult {
    data class Success(val recipeDetails: RecipeDetails) : RecipeDetailsResult()
    data object NoInternet : RecipeDetailsResult()
    data object Unknown : RecipeDetailsResult()
}