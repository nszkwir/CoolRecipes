package com.spitzer.data.local.recipe.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.spitzer.data.local.recipe.entities.RecipeDetailsEntity

@Dao
interface RecipeDetailsDao {

    @Query("SELECT * FROM recipe_details WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: Long): RecipeDetailsEntity?

    @Transaction
    @Upsert
    suspend fun upsert(recipe: RecipeDetailsEntity)
}
