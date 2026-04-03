package com.spitzer.data.di

import android.content.Context
import androidx.room.Room
import com.spitzer.data.local.recipe.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesRecipeDatabase(
        @ApplicationContext context: Context,
    ): RecipeDatabase = Room.databaseBuilder(
        context,
        RecipeDatabase::class.java,
        "recipes_database",
    ).build()
}
