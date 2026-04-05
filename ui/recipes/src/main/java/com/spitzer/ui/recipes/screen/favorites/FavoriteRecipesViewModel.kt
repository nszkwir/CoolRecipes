package com.spitzer.ui.recipes.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.usecase.favorites.GetFavoriteRecipesUseCase
import com.spitzer.ui.recipes.screen.list.views.RecipeListScreenSearchBarViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FavoriteRecipesViewModelOutput {
    data class RecipeDetail(val recipeId: Long) : FavoriteRecipesViewModelOutput()
}

@HiltViewModel
class FavoriteRecipesViewModel @Inject constructor(
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase
) : ViewModel() {

    lateinit var output: (FavoriteRecipesViewModelOutput) -> Unit

    private val searchQuery = MutableStateFlow("")

    private val _viewState by lazy {
        MutableStateFlow(
            FavoriteRecipesScreenViewState(
                recipeList = emptyList(),
                isLoading = true,
                searchBarViewState = RecipeListScreenSearchBarViewState(
                    isSearchActive = false,
                    isLoading = false,
                    isFunnelOn = false,
                    isFunnelEnabled = false,
                    query = "",
                    searchCriteria = RecipeSearchCriteria.NAME
                ),
                message = null
            )
        )
    }

    val viewState: StateFlow<FavoriteRecipesScreenViewState> by lazy {
        _viewState.asStateFlow()
    }

    init {
        /**
         * Observes the stream of favorite recipes from the data source and combines it with the current
         * search query. It filters the recipe list based on the search criteria and updates the
         * [_viewState] to reflect the current data and loading status.
         */
        viewModelScope.launch {
            // Combine the favorite recipes flow from DB with our local search query
            combine(
                getFavoriteRecipesUseCase(),
                searchQuery
            ) { favorites, query ->
                val filtered = if (query.trim().isEmpty()) {
                    favorites
                } else {
                    favorites.filter { it.title.contains(query, ignoreCase = true) }
                }
                Pair(favorites, filtered)
            }.collectLatest { (allFavorites, filteredFavorites) ->
                _viewState.update { currentState ->
                    currentState.copy(
                        recipeList = allFavorites,
                        isLoading = false,
                        searchBarViewState = currentState.searchBarViewState.copy(
                            recipesList = filteredFavorites,
                            isLoading = false
                        )
                    )
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        searchQuery.value = query
        _viewState.update { currentState ->
            currentState.copy(
                searchBarViewState = currentState.searchBarViewState.copy(
                    query = query,
                    isLoading = false // Local search is practically instant
                )
            )
        }
    }

    fun onOpenSearch() {
        _viewState.update { currentState ->
            currentState.copy(
                searchBarViewState = currentState.searchBarViewState.copy(
                    isSearchActive = true,
                    recipesList = if (searchQuery.value.trim()
                            .isEmpty()
                    ) currentState.recipeList.filterNotNull() else currentState.searchBarViewState.recipesList
                )
            )
        }
    }

    fun onCloseSearch() {
        searchQuery.value = ""
        _viewState.update { currentState ->
            currentState.copy(
                searchBarViewState = currentState.searchBarViewState.copy(
                    isSearchActive = false,
                    query = "",
                    recipesList = emptyList()
                )
            )
        }
    }

    fun onRecipeCardClicked(recipe: Recipe) {
        output(FavoriteRecipesViewModelOutput.RecipeDetail(recipe.id))
    }

    fun refreshFavorites() {
        _viewState.update { it.copy(isLoading = false) }
    }

    fun onMessagePrimaryButtonClicked() {}

    fun onMessageSecondaryButtonClicked() {}
}
