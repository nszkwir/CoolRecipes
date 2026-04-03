package com.spitzer.ui.recipes.screen.favorites

import androidx.compose.runtime.Composable
import com.spitzer.designsystem.views.message.MessageViewMapper
import com.spitzer.designsystem.views.message.MessageViewState
import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.ui.recipes.screen.list.views.RecipeListScreenSearchBarViewState

import androidx.compose.runtime.Immutable

@Immutable
data class FavoriteRecipesScreenViewState(
    val recipeList: List<Recipe?>,
    val isLoading: Boolean,
    val searchBarViewState: RecipeListScreenSearchBarViewState,
    val message: Message?
) {
    @Immutable
    data class Message(
        val type: Type
    ) {
        enum class Type {
            NO_FAVORITES, GENERIC
        }
    }

    @Composable
    fun mapMessage(
        onPrimaryButtonClicked: () -> Unit,
        onSecondaryButtonClicked: (() -> Unit)? = null
    ): MessageViewState? {
        // Here we can map custom empty states if we want, but typically NO_FAVORITES 
        // string isn't defined yet, so we'll just handle it gracefully or use generic
        return when (this.message?.type) {
            Message.Type.NO_FAVORITES -> MessageViewMapper.genericErrorMessage(
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

    // Favorites don't strictly need a funnel if we aren't supporting bottom sheet filters 
    fun isFunnelOn(): Boolean {
        return false
    }
}
