package com.spitzer.ui.recipes.screen.list

import androidx.compose.runtime.Composable
import com.spitzer.designsystem.views.message.MessageViewMapper
import com.spitzer.designsystem.views.message.MessageViewState
import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.ui.recipes.screen.list.views.RecipeListBottomSheetViewState
import com.spitzer.ui.recipes.screen.list.views.RecipeListScreenSearchBarViewState

import androidx.compose.runtime.Immutable

@Immutable
data class RecipeListScreenViewState(
    val recipeList: List<Recipe?>,
    val isLoading: Boolean,
    val searchBarViewState: RecipeListScreenSearchBarViewState,
    val bottomSheetViewState: RecipeListBottomSheetViewState,
    val message: Message?
) {
    @Immutable
    data class Message(
        val type: Type
    ) {
        enum class Type {
            NO_INTERNET, GENERIC
        }
    }

    @Composable
    fun mapMessage(
        onPrimaryButtonClicked: () -> Unit,
        onSecondaryButtonClicked: (() -> Unit)? = null
    ): MessageViewState? {
        return when (this.message?.type) {
            Message.Type.NO_INTERNET -> MessageViewMapper.noInternetConnectionMessage(
                onPrimaryButtonClicked = onPrimaryButtonClicked,
                onSecondaryButtonClicked = onSecondaryButtonClicked
            )

            Message.Type.GENERIC -> MessageViewMapper.genericErrorMessage(
                onPrimaryButtonClicked = onPrimaryButtonClicked,
                onSecondaryButtonClicked = onSecondaryButtonClicked
            )

            null -> null
        }
    }
}
