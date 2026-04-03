package com.spitzer.data.remote.recipe.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipePageResponse(
    val results: List<RecipeResponse>, val totalResults: Int
)
