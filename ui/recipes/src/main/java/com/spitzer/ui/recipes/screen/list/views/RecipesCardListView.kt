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
