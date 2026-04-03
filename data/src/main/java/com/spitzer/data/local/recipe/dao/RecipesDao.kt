package com.spitzer.data.local.recipe.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spitzer.data.local.recipe.entities.RecipeEntity

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipe")
    fun get(): List<RecipeEntity>

    @Upsert
    suspend fun upsert(recipeEntities: List<RecipeEntity>)

    @Query("DELETE FROM recipe")
    suspend fun deleteAll()
}
