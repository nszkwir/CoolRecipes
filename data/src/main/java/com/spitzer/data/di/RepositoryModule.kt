package com.spitzer.data.di

import com.spitzer.data.local.recipe.dao.FavoriteRecipeDao
import com.spitzer.data.local.recipe.dao.RecipeDetailsDao
import com.spitzer.data.local.recipe.dao.RecipesDao
import com.spitzer.data.local.recipe.sharedpreferences.RecipeSharedPreferences
import com.spitzer.data.remote.recipe.api.RecipeService
import com.spitzer.data.repository.connectivity.ConnectivityRepositoryImpl
import com.spitzer.data.repository.recipe.RecipeRepositoryImpl
import com.spitzer.data.repository.settings.SettingsRepositoryImpl
import com.spitzer.domain.repository.ConnectivityRepository
import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleBinder {
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindConnectivityRepository(
        connectivityRepositoryImpl: ConnectivityRepositoryImpl
    ): ConnectivityRepository

}

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModuleProvider {
    @Provides
    @Singleton
    fun provideRecipeRepository(
        recipeService: RecipeService,
        recipesDao: RecipesDao,
        recipeDetailsDao: RecipeDetailsDao,
        favoriteRecipeDao: FavoriteRecipeDao,
        recipePreferences: RecipeSharedPreferences
    ): RecipeRepository {
        return RecipeRepositoryImpl(
            recipeService = recipeService,
            recipesDao = recipesDao,
            recipeDetailsDao = recipeDetailsDao,
            favoriteRecipeDao = favoriteRecipeDao,
            recipePreferences = recipePreferences,
            ioDispatcher = Dispatchers.IO
        )
    }
}
