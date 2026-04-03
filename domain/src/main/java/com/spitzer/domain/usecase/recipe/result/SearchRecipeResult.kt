package com.spitzer.domain.usecase.recipe.result

import com.spitzer.domain.model.recipe.Recipe

sealed class SearchRecipeResult {
    data class Success(val recipeList: List<Recipe>) : SearchRecipeResult()
    data object NoInternet : SearchRecipeResult()
    data object Unknown : SearchRecipeResult()
}