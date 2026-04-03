package com.spitzer.data.local.recipe

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spitzer.data.local.recipe.dao.FavoriteRecipeDao
import com.spitzer.data.local.recipe.dao.RecipeDetailsDao
import com.spitzer.data.local.recipe.dao.RecipesDao
import com.spitzer.data.local.recipe.entities.FavoriteRecipeEntity
import com.spitzer.data.local.recipe.entities.RecipeEntity
import com.spitzer.data.local.recipe.entities.RecipeDetailsEntity
import com.spitzer.data.local.recipe.typeconverters.StringListConverters

@Database(
    entities = [RecipeEntity::class, RecipeDetailsEntity::class, FavoriteRecipeEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    StringListConverters::class
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipesDao(): RecipesDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
    abstract fun recipeDetailsDao(): RecipeDetailsDao
}
