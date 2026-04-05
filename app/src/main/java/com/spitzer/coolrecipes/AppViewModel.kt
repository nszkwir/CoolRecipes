package com.spitzer.coolrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject



/**
 * Core [ViewModel] for the application responsible for managing global state
 * and performing initial data synchronization.
 *
 * This ViewModel handles application-wide preferences such as the theme settings
 * and ensures that the [RecipeRepository] is initialized when the app starts.
 *
 * @property settingsRepository Repository for accessing and managing user settings.
 * @property recipeRepository Repository for accessing and initializing recipe data.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val isDarkTheme = settingsRepository.isDarkTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        viewModelScope.launch {
            recipeRepository.initialize()
        }
    }
}
