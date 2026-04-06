package com.spitzer.ui.recipes.screen.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spitzer.domain.model.recipe.RecipeDetails
import com.spitzer.domain.usecase.favorites.GetRecipeFavoriteStatusUseCase
import com.spitzer.domain.usecase.favorites.SetRecipeFavoriteStatusUseCase
import com.spitzer.domain.usecase.favorites.result.SetRecipeFavoriteStatusResult
import com.spitzer.domain.usecase.recipedetails.GetRecipeDetailsByIdUseCase
import com.spitzer.domain.usecase.recipedetails.RefreshRecipeDetailsByIdUseCase
import com.spitzer.domain.usecase.recipedetails.result.RecipeDetailsResult
import com.spitzer.ui.recipes.screen.details.views.RecipeDetailsViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeDetailsScreenViewModelInput(
    val recipeId: Long
)

sealed class RecipeDetailsScreenViewModelOutput {
    data object ScreenNavigateBack : RecipeDetailsScreenViewModelOutput()
}

@HiltViewModel(assistedFactory = RecipeDetailsScreenViewModel.Factory::class)
class RecipeDetailsScreenViewModel @AssistedInject constructor(
    private val getRecipeByIdUseCase: GetRecipeDetailsByIdUseCase,
    private val refreshRecipeDetailsByIdUseCase: RefreshRecipeDetailsByIdUseCase,
    private val getRecipeFavoriteStatusUseCase: GetRecipeFavoriteStatusUseCase,
    private val setRecipeFavoriteStatusUseCase: SetRecipeFavoriteStatusUseCase,
    @Assisted val input: RecipeDetailsScreenViewModelInput
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(input: RecipeDetailsScreenViewModelInput): RecipeDetailsScreenViewModel
    }

    lateinit var output: (RecipeDetailsScreenViewModelOutput) -> Unit

    private val _viewState = MutableStateFlow(RecipeDetailsScreenViewState())
    val viewState: StateFlow<RecipeDetailsScreenViewState> = _viewState.asStateFlow()

    private var isCurrentlyFavorite = false

    init {
        loadRecipeDetails()
        viewModelScope.launch {
            getRecipeFavoriteStatusUseCase(input.recipeId).collectLatest {
                isCurrentlyFavorite = it
                _viewState.update { currentState ->
                    currentState.copy(
                        recipeDetails = currentState.recipeDetails?.copy(
                            isFavorite = it
                        )
                    )
                }
            }
        }
    }

    fun onRefresh() {
        refreshRecipeDetails()
    }

    fun onBackButtonPressed() {
        output(RecipeDetailsScreenViewModelOutput.ScreenNavigateBack)
    }

    fun onFavoriteTapped() {
        val details = _viewState.value.recipeDetails ?: return
        val isFavorite = !details.isFavorite
        viewModelScope.launch {
            val result = setRecipeFavoriteStatusUseCase(
                id = input.recipeId,
                isFavorite = isFavorite,
                title = details.title ?: "",
                image = details.imageUrl?.toString(),
                summary = details.summary ?: ""
            )
            when (result) {
                SetRecipeFavoriteStatusResult.Error -> {
                    showGenericError()
                }

                is SetRecipeFavoriteStatusResult.Success -> {
                    //Favorite state is updated in the getRecipeFavoriteStatusUseCase
                }
            }
        }
    }

    fun onMessagePrimaryButtonClicked() {
        loadRecipeDetails()
    }

    fun onMessageSecondaryButtonClicked() {
        if (_viewState.value.recipeDetails != null) {
            removeErrorView()
        } else {
            output(RecipeDetailsScreenViewModelOutput.ScreenNavigateBack)
        }
    }

    private fun inputChanged(recipeDetails: RecipeDetails) {
        removeErrorView()

        _viewState.update { currentState ->
            currentState.copy(
                recipeDetails = RecipeDetailsViewState(
                    imageUrl = recipeDetails.image,
                    title = recipeDetails.title,
                    summary = recipeDetails.summary,
                    instructions = recipeDetails.instructions,
                    readyInMinutes = recipeDetails.readyInMinutes,
                    servings = recipeDetails.servings,
                    calories = recipeDetails.healthScore,
                    vegan = recipeDetails.vegan,
                    spoonacularScore = recipeDetails.spoonacularScore,
                    spoonacularSourceUrl = recipeDetails.spoonacularSourceUrl,
                    ingredients = recipeDetails.ingredients,
                    isFavorite = isCurrentlyFavorite
                )
            )
        }
    }

    /**
     * Loads the recipe details for the specific recipe ID provided in the input.
     *
     * This function triggers the loading state, fetches recipe data via [getRecipeByIdUseCase],
     * and handles the response by either updating the view state with recipe information
     * or displaying appropriate error messages (e.g., no internet or unknown errors).
     * Finally, it hides the loading animation once the process is complete.
     */
    private fun loadRecipeDetails() {
        showLoadingAnimation()
        viewModelScope.launch {
            when (val result = getRecipeByIdUseCase(input.recipeId)) {
                RecipeDetailsResult.NoInternet -> {
                    showNoInternetConnectionError()
                }

                RecipeDetailsResult.Unknown -> {
                    showGenericError()
                }

                is RecipeDetailsResult.Success -> {
                    inputChanged(recipeDetails = result.recipeDetails)
                }
            }
            hideLoadingAnimation()
        }
    }

    /**
     * Refreshes the recipe details by fetching the latest data from the remote source.
     *
     * This function displays a loading animation and executes the [refreshRecipeDetailsByIdUseCase].
     * Based on the result, it either updates the view state with the new details or
     * displays an appropriate error message (e.g., no internet or unknown error).
     */
    private fun refreshRecipeDetails() {
        showLoadingAnimation()
        viewModelScope.launch {
            when (val result = refreshRecipeDetailsByIdUseCase(input.recipeId)) {
                RecipeDetailsResult.NoInternet -> {
                    showNoInternetConnectionError()
                }

                RecipeDetailsResult.Unknown -> {
                    showGenericError()
                }

                is RecipeDetailsResult.Success -> {
                    inputChanged(recipeDetails = result.recipeDetails)
                }
            }
            hideLoadingAnimation()
        }
    }

    /** Loading States */
    private fun showLoadingAnimation() {
        _viewState.update { currentState ->
            currentState.copy(
                message = null,
                isLoading = true
            )
        }
    }

    private fun hideLoadingAnimation() {
        _viewState.update { currentState ->
            currentState.copy(
                isLoading = false
            )
        }
    }

    /** Error messages */
    fun showNoInternetConnectionError() {
        _viewState.update { currentState ->
            currentState.copy(
                message = RecipeDetailsScreenViewState.Message(
                    type = RecipeDetailsScreenViewState.Message.Type.NO_INTERNET
                )
            )
        }
    }

    fun showGenericError() {
        _viewState.update { currentState ->
            currentState.copy(
                message = RecipeDetailsScreenViewState.Message(
                    type = RecipeDetailsScreenViewState.Message.Type.GENERIC
                )
            )
        }
    }

    private fun removeErrorView() {
        _viewState.update { currentState ->
            currentState.copy(
                message = null
            )
        }
    }
}