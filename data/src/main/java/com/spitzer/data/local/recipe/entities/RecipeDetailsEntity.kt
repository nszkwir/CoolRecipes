package com.spitzer.data.local.recipe.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_details",
)
data class RecipeDetailsEntity(
    @PrimaryKey
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
    val diets: List<String>,
    val spoonacularScore: Double,
    val spoonacularSourceUrl: String?,
    val ingredients: List<String>
)
