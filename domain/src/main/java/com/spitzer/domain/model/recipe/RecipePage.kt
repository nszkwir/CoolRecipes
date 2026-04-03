package com.spitzer.domain.model.recipe

data class RecipePage(
    val list: List<Recipe?>,
    val totalResults: Int
)
