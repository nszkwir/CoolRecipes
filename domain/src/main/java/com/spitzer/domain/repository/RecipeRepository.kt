package com.spitzer.domain.repository

import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.model.recipe.RecipePage
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.domain.usecase.favorites.result.SetRecipeFavoriteStatusResult
import com.spitzer.domain.usecase.recipe.result.RecipePaginationResult
import com.spitzer.domain.usecase.recipe.result.SearchRecipeResult
import com.spitzer.domain.usecase.recipedetails.result.RecipeDetailsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RecipeRepository {
    val recipePage: StateFlow<RecipePage>
    val favoriteRecipes: Flow<List<Recipe>>
    val favoriteRecipeIds: Flow<Set<Long>>

    suspend fun initialize()

    suspend fun setRecipeFavorite(
        id: Long,
        isFavorite: Boolean,
        title: String,
        image: String?,
        summary: String
    ): SetRecipeFavoriteStatusResult

    suspend fun refreshRecipeList(
        sortCriteria: RecipeSortCriteria,
        sortOrder: RecipeSortOrder
    ): RecipePaginationResult

    suspend fun fetchRecipeList(
        elementIndex: Int,
        sortCriteria: RecipeSortCriteria,
        sortOrder: RecipeSortOrder
    ): RecipePaginationResult

    suspend fun searchRecipeList(
        query: String,
        searchCriteria: RecipeSearchCriteria,
        sortCriteria: RecipeSortCriteria,
        sortOrder: RecipeSortOrder
    ): SearchRecipeResult

    suspend fun getRecipeDetailsById(id: Long): RecipeDetailsResult
    suspend fun fetchRecipeDetails(id: Long): RecipeDetailsResult
}
