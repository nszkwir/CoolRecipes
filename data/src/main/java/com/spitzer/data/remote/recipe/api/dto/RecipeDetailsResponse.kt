package com.spitzer.data.remote.recipe.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailsResponse(
    val id: Long,
    val title: String,
    val readyInMinutes: Int,
    val servings: Int,
    val summary: String,
    val instructions: String,
    val vegetarian: Boolean,
    val vegan: Boolean,
    val glutenFree: Boolean,
    val dairyFree: Boolean,
    val image: String?,
    val healthScore: Double?,
    val diets: List<String>?,
    val spoonacularScore: Double,
    val spoonacularSourceUrl: String?,
    val extendedIngredients: List<Ingredient>?
) {
    @Serializable
    data class Ingredient(
        val original: String?,
        val image: String?
    )
}
