package com.spitzer.ui.recipes.screen.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.spitzer.designsystem.views.message.MessageViewMapper
import com.spitzer.designsystem.views.message.MessageViewState
import com.spitzer.ui.recipes.screen.details.views.RecipeDetailsViewState

@Immutable
data class RecipeDetailsScreenViewState(
    val recipeDetails: RecipeDetailsViewState? = null,
    val isLoading: Boolean = true,
    val message: Message? = null
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
        onSecondaryButtonClicked: (() -> Unit)?
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
