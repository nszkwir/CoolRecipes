package com.spitzer.ui.recipes.screen.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spitzer.designsystem.animations.rememberDuration
import com.spitzer.designsystem.components.LoadingView
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing
import com.spitzer.designsystem.views.message.MessageView
import com.spitzer.ui.recipes.screen.list.views.EmptySearchView
import com.spitzer.ui.recipes.screen.list.views.RecipeListScreenSearchBarView
import com.spitzer.ui.recipes.screen.list.views.RecipesCardListView
import com.spitzer.ui.recipes.screen.list.views.mapCardListViewStates
import com.spitzer.domain.model.recipe.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteRecipesScreen(
    modifier: Modifier = Modifier,
    output: (FavoriteRecipesViewModelOutput) -> Unit
) {
    val viewModel: FavoriteRecipesViewModel = hiltViewModel()
    viewModel.output = output

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    FavoriteRecipesScreen(
        modifier = modifier,
        viewState = viewState,
        onRefresh = viewModel::refreshFavorites,
        onRecipeCardClicked = viewModel::onRecipeCardClicked,
        onQueryChange = viewModel::onQueryChange,
        onOpenSearch = viewModel::onOpenSearch,
        onCloseSearch = viewModel::onCloseSearch,
        onMessagePrimaryButtonClicked = viewModel::onMessagePrimaryButtonClicked,
        onMessageSecondaryButtonClicked = viewModel::onMessageSecondaryButtonClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteRecipesScreen(
    modifier: Modifier = Modifier,
    viewState: FavoriteRecipesScreenViewState,
    onRefresh: () -> Unit,
    onRecipeCardClicked: (Recipe) -> Unit,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onMessagePrimaryButtonClicked: () -> Unit,
    onMessageSecondaryButtonClicked: () -> Unit
) {
    val state = rememberPullToRefreshState()
    val message = viewState.mapMessage(
        onPrimaryButtonClicked = onMessagePrimaryButtonClicked,
        onSecondaryButtonClicked = onMessageSecondaryButtonClicked
    )
    val showLoading = rememberDuration(value = viewState.isLoading, minDurationMillis = 700)

    Box(
        modifier = modifier
            .pullToRefresh(
                isRefreshing = false,
                state = state,
                onRefresh = onRefresh
            )
            .fillMaxSize()
            .background(CoolRecipesTheme.colors.n99n00)
    ) {
        when {
            showLoading -> LoadingView()
            message != null -> MessageView(viewState = message)
            else -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.FOUR.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    RecipeListScreenSearchBarView(
                        modifier = Modifier.padding(horizontal = Spacing.FOUR.dp),
                        viewState = viewState.searchBarViewState,
                        onRecipeCardClicked = onRecipeCardClicked,
                        onFunnelTap = null,
                        onQueryChange = onQueryChange,
                        onOpenSearch = onOpenSearch,
                        onCloseSearch = onCloseSearch
                    )

                    if (viewState.recipeList.isNotEmpty()) {
                        RecipesCardListView(
                            cardListViewStates = mapCardListViewStates(
                                recipeList = viewState.recipeList,
                                onCardClicked = onRecipeCardClicked
                            ),
                            onPrefetchItemsAtIndex = null
                        )
                    } else {
                        EmptySearchView()
                    }
                }
            }
        }

        // We handle the refresh status with a custom animation
        // The indicator is added to provide feedback to the user regarding
        // the refresh action will occur when swiping down
        PullToRefreshDefaults.Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = false,
            state = state
        )
    }
}
