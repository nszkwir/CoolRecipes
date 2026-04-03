package com.spitzer.data.local.recipe.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe",
)
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val index: Long? = null,
    val id: Long,
    val title: String,
    val image: String?,
    val summary: String
)
