package com.spitzer.data.remote.recipe.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    val id: Long,
    val title: String,
    val image: String,
    val summary: String
)
