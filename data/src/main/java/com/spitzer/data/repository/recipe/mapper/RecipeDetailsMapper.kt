package com.spitzer.data.repository.recipe.mapper

import com.spitzer.data.remote.recipe.api.dto.RecipeDetailsResponse
import com.spitzer.data.local.recipe.entities.RecipeDetailsEntity
import com.spitzer.domain.model.recipe.RecipeDetails
import java.net.MalformedURLException
import java.net.URL

object RecipeDetailsMapper {
    fun mapFromRecipeDetailsResponse(response: RecipeDetailsResponse): RecipeDetails {
        return RecipeDetails(
            id = response.id,
            title = response.title,
            readyInMinutes = response.readyInMinutes,
            servings = response.servings,
            summary = response.summary,
            instructions = response.instructions,
            vegetarian = response.vegetarian,
            vegan = response.vegan,
            glutenFree = response.glutenFree,
            dairyFree = response.dairyFree,
            image = parseUrl(response.image),
            healthScore = response.healthScore,
            diets = response.diets ?: emptyList(),
            spoonacularScore = response.spoonacularScore,
            spoonacularSourceUrl = parseUrl(response.spoonacularSourceUrl),
            ingredients = response.extendedIngredients?.mapNotNull { it.original } ?: emptyList()
        )
    }

    fun mapFromStoredRecipeDetails(recipeDetailsEntity: RecipeDetailsEntity): RecipeDetails {
        return RecipeDetails(
            id = recipeDetailsEntity.id,
            title = recipeDetailsEntity.title,
            readyInMinutes = recipeDetailsEntity.readyInMinutes,
            servings = recipeDetailsEntity.servings,
            summary = recipeDetailsEntity.summary,
            instructions = recipeDetailsEntity.instructions,
            vegetarian = recipeDetailsEntity.vegetarian,
            vegan = recipeDetailsEntity.vegan,
            glutenFree = recipeDetailsEntity.glutenFree,
            dairyFree = recipeDetailsEntity.dairyFree,
            image = parseUrl(recipeDetailsEntity.image),
            healthScore = recipeDetailsEntity.healthScore,
            diets = recipeDetailsEntity.diets,
            spoonacularScore = recipeDetailsEntity.spoonacularScore,
            spoonacularSourceUrl = parseUrl(recipeDetailsEntity.spoonacularSourceUrl),
            ingredients = recipeDetailsEntity.ingredients
        )
    }

    fun mapToStoredRecipeDetails(recipeDetails: RecipeDetails): RecipeDetailsEntity {
        return RecipeDetailsEntity(
            id = recipeDetails.id,
            title = recipeDetails.title,
            readyInMinutes = recipeDetails.readyInMinutes,
            servings = recipeDetails.servings,
            summary = recipeDetails.summary,
            instructions = recipeDetails.instructions,
            vegetarian = recipeDetails.vegetarian,
            vegan = recipeDetails.vegan,
            glutenFree = recipeDetails.glutenFree,
            dairyFree = recipeDetails.dairyFree,
            image = recipeDetails.image?.toString() ?: "",
            healthScore = recipeDetails.healthScore,
            diets = recipeDetails.diets ?: emptyList(),
            spoonacularScore = recipeDetails.spoonacularScore,
            spoonacularSourceUrl = recipeDetails.spoonacularSourceUrl?.toString() ?: "",
            ingredients = recipeDetails.ingredients
        )
    }

    private fun parseUrl(urlString: String?): URL? {
        return try {
            URL(urlString)
        } catch (e: MalformedURLException) {
            null
        }
    }
}
