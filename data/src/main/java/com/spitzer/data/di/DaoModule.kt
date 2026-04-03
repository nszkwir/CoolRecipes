package com.spitzer.data.di

import com.spitzer.data.local.recipe.RecipeDatabase
import com.spitzer.data.local.recipe.dao.FavoriteRecipeDao
import com.spitzer.data.local.recipe.dao.RecipeDetailsDao
import com.spitzer.data.local.recipe.dao.RecipesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    @Singleton
    fun providesFavoriteRecipeDao(
        database: RecipeDatabase,
    ): FavoriteRecipeDao = database.favoriteRecipeDao()

    @Provides
    @Singleton
    fun providesRecipeDetailsDao(
        database: RecipeDatabase,
    ): RecipeDetailsDao = database.recipeDetailsDao()

    @Provides
    @Singleton
    fun providesRecipesDao(
        database: RecipeDatabase,
    ): RecipesDao = database.recipesDao()

}
