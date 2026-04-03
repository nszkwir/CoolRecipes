package com.spitzer.data.local.recipe.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spitzer.data.local.recipe.entities.FavoriteRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRecipeDao {

    @Query("SELECT * FROM favorite_recipe")
    fun getAll(): Flow<List<FavoriteRecipeEntity>>

    @Query("SELECT id FROM favorite_recipe")
    fun getAllIds(): Flow<List<Long>>

    @Upsert
    suspend fun upsert(recipe: FavoriteRecipeEntity)

    @Query("DELETE FROM favorite_recipe WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM favorite_recipe")
    suspend fun deleteAll()
}
