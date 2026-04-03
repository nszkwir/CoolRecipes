package com.spitzer.data.di

import android.content.Context
import com.spitzer.data.local.recipe.sharedpreferences.RecipeSharedPreferences
import com.spitzer.data.local.recipe.sharedpreferences.RecipeSharedPreferencesImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideRecipesSharedPreferences(@ApplicationContext context: Context): RecipeSharedPreferences {
        return RecipeSharedPreferencesImpl(context)
    }
}
