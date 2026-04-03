package com.spitzer.domain.model.recipe

import java.net.URL

data class RecipeDetails(
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
    val image: URL?,
    val healthScore: Double?,
    val diets: List<String>?,
    val spoonacularScore: Double,
    val spoonacularSourceUrl: URL?,
    val ingredients: List<String>
)
