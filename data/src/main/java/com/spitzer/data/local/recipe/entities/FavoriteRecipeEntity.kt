package com.spitzer.data.local.recipe.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_recipe",
)
data class FavoriteRecipeEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val image: String?,
    val summary: String
)
