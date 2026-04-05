package com.spitzer.ui.recipes.screen.list.views

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.spitzer.designsystem.R
import com.spitzer.designsystem.components.RecipeCardViewState
import com.spitzer.designsystem.components.RecipeCardView
import com.spitzer.designsystem.data.AnnouncedAction
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing
import com.spitzer.domain.model.recipe.Recipe
import kotlinx.coroutines.flow.collectLatest

/**
 * A composable that displays a scrollable list of recipe cards.
 *
 * This component utilizes a [LazyColumn] to efficiently render a list of recipes based on the
 * provided [cardListViewStates]. It also includes a prefetching mechanism to notify when the
 * user scrolls near the end of the list, facilitating pagination or infinite scrolling.
 *
 * @param modifier The [Modifier] to be applied to the layout.
 * @param cardListViewStates The list of view states representing each recipe card (e.g., content or loading states).
 * @param onPrefetchItemsAtIndex An optional callback triggered with the index of the last visible item,
 * used to request more data as the user scrolls.
 */
@Composable
fun RecipesCardListView(
    modifier: Modifier = Modifier,
    cardListViewStates: List<RecipeCardViewState>,
    onPrefetchItemsAtIndex: ((Int) -> Unit)?
) {
    val listState = rememberLazyListState()
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = Spacing.FOUR.dp)
                    .background(Color.Transparent),
                state = listState
            ) {
                items(cardListViewStates.size) { index ->
                    RecipeCardView(
                        modifier = Modifier.padding(bottom = Spacing.FOUR.dp),
                        viewState = cardListViewStates[index]
                    )
                }
            }
        }

        // Observe scroll state to load more items
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }.collectLatest {
                val lastItem = it.lastOrNull()
                lastItem?.let { onPrefetchItemsAtIndex?.invoke(it.index) }
            }
        }
    }
}

/**
 * Maps a list of [Recipe] domain models to a list of [RecipeCardViewState] objects.
 *
 * This function transforms the raw recipe data into a format suitable for the UI, handling
 * both valid recipe content and null values (which are mapped to a loading state).
 *
 * @param recipeList The list of [Recipe] objects to be mapped. Null elements in the list
 * represent items currently in a loading state.
 * @param onCardClicked A callback triggered when a recipe card is tapped, passing the selected [Recipe].
 * @return A list of [RecipeCardViewState] containing either content or loading states.
 */
@Composable
fun mapCardListViewStates(
    recipeList: List<Recipe?>,
    onCardClicked: (Recipe) -> Unit
): List<RecipeCardViewState> {
    return recipeList.map { recipe ->
        recipe?.let {
            RecipeCardViewState.Content(
                topImageURL = it.image,
                firstTitle = it.title,
                secondTitle = it.summary,
                onTap = AnnouncedAction(
                    action = { onCardClicked(recipe) },
                    description = stringResource(
                        R.string.recipe_list_screen_see_card_details_content_description,
                        it.title
                    )
                )
            )
        } ?: run {
            RecipeCardViewState.Loading
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewRecipesCardListView() {
    CoolRecipesTheme {
        RecipesCardListView(
            cardListViewStates = mapCardListViewStates(
                recipeList = listOf(
                    Recipe(
                        id = 1_000,
                        title = "Title 1",
                        image = null,
                        summary = "Summary"
                    ),
                    Recipe(
                        id = 2_000,
                        title = "Title 2",
                        image = null,
                        summary = "Summary 2"
                    ),
                    Recipe(
                        id = 3_000,
                        title = "Title 3",
                        image = null,
                        summary = "Summary 3"
                    )
                ),
                onCardClicked = {}
            ),
            onPrefetchItemsAtIndex = {}
        )
    }
}
