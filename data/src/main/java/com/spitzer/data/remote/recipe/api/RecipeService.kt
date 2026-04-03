package com.spitzer.data.remote.recipe.api

import com.spitzer.data.remote.common.ApiCaller
import com.spitzer.data.remote.common.ApiResult
import com.spitzer.data.remote.recipe.api.dto.RecipeDetailsResponse
import com.spitzer.data.remote.recipe.api.dto.RecipePageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

interface SpoonacularService {

    @GET("recipes/complexSearch")
    suspend fun fetchRecipes(
        @Query("offset") offset: Int,
        @Query("number") limit: Int,
        @Query("query") query: String?,
        @Query("includeIngredients") includeIngredients: String?,
        @Query("addRecipeInformation") addRecipeInformation: Boolean,
        @Query("sort") sort: String?,
        @Query("sortDirection") sortDirection: String
    ): Response<RecipePageResponse>

    @GET("recipes/{id}/information")
    suspend fun fetchRecipeDetails(
        @Path("id") id: Long,
        @Query("includeNutrition") includeNutrition: Boolean = false,
        @Query("addWinePairing") addWinePairing: Boolean = false,
        @Query("addTasteData") addTasteData: Boolean = false,
    ): Response<RecipeDetailsResponse>
}

interface RecipeService {
    suspend fun fetchRecipes(
        offset: Int,
        limit: Int,
        query: String?,
        includeIngredients: String?,
        sortCriteria: String?,
        sortOrder: String
    ): ApiResult<RecipePageResponse>

    suspend fun fetchRecipeDetails(
        id: Long
    ): ApiResult<RecipeDetailsResponse>
}

internal class RecipeServiceImpl @Inject constructor(
    private val apiService: SpoonacularService,
    private val apiCaller: ApiCaller
) : RecipeService {
    override suspend fun fetchRecipes(
        offset: Int,
        limit: Int,
        query: String?,
        includeIngredients: String?,
        sortCriteria: String?,
        sortOrder: String
    ): ApiResult<RecipePageResponse> =
        apiCaller.safeApiCall {
            apiService.fetchRecipes(
                offset = offset,
                limit = limit,
                query = query,
                includeIngredients = includeIngredients,
                addRecipeInformation = true,
                sort = sortCriteria,
                sortDirection = sortOrder
            )
        }

    override suspend fun fetchRecipeDetails(id: Long): ApiResult<RecipeDetailsResponse> =
        apiCaller.safeApiCall {
            apiService.fetchRecipeDetails(id = id)
        }
}
