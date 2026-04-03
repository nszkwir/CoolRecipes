package com.spitzer.ui.recipes.screen.list

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import android.content.res.Configuration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.spitzer.designsystem.animations.rememberDuration
import com.spitzer.designsystem.components.LoadingView
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing
import com.spitzer.designsystem.views.message.MessageView
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.ui.recipes.screen.list.views.EmptySearchView
import com.spitzer.ui.recipes.screen.list.views.RecipeListBottomSheetViewState
import com.spitzer.ui.recipes.screen.list.views.RecipeListScreenSearchBarViewState
import com.spitzer.ui.recipes.screen.list.views.RecipeListBottomSheetView
import com.spitzer.ui.recipes.screen.list.views.RecipeListScreenSearchBarView
import com.spitzer.ui.recipes.screen.list.views.RecipesCardListView
import com.spitzer.ui.recipes.screen.list.views.mapCardListViewStates
import com.spitzer.domain.model.recipe.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    modifier: Modifier = Modifier,
    output: (RecipeListScreenViewModelOutput) -> Unit
) {
    val viewModel: RecipeListScreenViewModel = hiltViewModel()
    viewModel.output = output

    val viewState: RecipeListScreenViewState by viewModel.viewState.collectAsState()
    RecipeListScreen(
        modifier = modifier,
        viewState = viewState,
        onRefresh = viewModel::refreshRecipeList,
        onRecipeCardClicked = viewModel::onRecipeCardClicked,
        onPrefetchItemsAtIndex = viewModel::getRecipeList,
        onFunnelTap = viewModel::onFunnelTap,
        onQueryChange = viewModel::onQueryChange,
        onOpenSearch = viewModel::onOpenSearch,
        onCloseSearch = viewModel::onCloseSearch,
        onSearchCriteriaSelected = viewModel::onSearchCriteriaSelected,
        onSortCriteriaSelected = viewModel::onSortCriteriaSelected,
        onSortOrderSelected = viewModel::onSortOrderSelected,
        onClear = viewModel::clearSearchFilters,
        onConfirm = viewModel::confirmSearchFilters,
        onDismissBottomSheet = viewModel::onDismissBottomSheet,
        onMessagePrimaryButtonClicked = viewModel::onMessagePrimaryButtonClicked,
        onMessageSecondaryButtonClicked = viewModel::onMessageSecondaryButtonClicked
    )
}

@Composable
fun RecipeListScreen(
    modifier: Modifier = Modifier,
    viewState: RecipeListScreenViewState,
    onRefresh: () -> Unit,
    onRecipeCardClicked: (Recipe) -> Unit,
    onPrefetchItemsAtIndex: (Int) -> Unit,
    onFunnelTap: () -> Unit,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onSearchCriteriaSelected: (RecipeSearchCriteria) -> Unit,
    onSortCriteriaSelected: (RecipeSortCriteria) -> Unit,
    onSortOrderSelected: (RecipeSortOrder) -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit,
    onDismissBottomSheet: () -> Unit,
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.FOUR.dp),
                ) {
                    RecipeListScreenSearchBarView(
                        modifier = Modifier.padding(horizontal = Spacing.FOUR.dp),
                        viewState = viewState.searchBarViewState,
                        onRecipeCardClicked = onRecipeCardClicked,
                        onFunnelTap = onFunnelTap,
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
                            onPrefetchItemsAtIndex = onPrefetchItemsAtIndex
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

        if (!viewState.bottomSheetViewState.shouldHide) {
            RecipeListBottomSheetView(
                viewState = viewState.bottomSheetViewState,
                onSearchCriteriaSelected = onSearchCriteriaSelected,
                onSortCriteriaSelected = onSortCriteriaSelected,
                onSortOrderSelected = onSortOrderSelected,
                onDismiss = onDismissBottomSheet,
                onClear = onClear,
                onConfirm = onConfirm
            )
        }
    }
}

@Preview(name = "Empty Recipes - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Empty Recipes - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewEmptyRecipes() {
    CoolRecipesTheme {
        RecipeListScreen(
            viewState = RecipeListScreenViewState(
                recipeList = emptyList(),
                isLoading = false,
                searchBarViewState = RecipeListScreenSearchBarViewState(
                    isSearchActive = false,
                    isFunnelOn = false,
                    isFunnelEnabled = true,
                    isLoading = false,
                    query = ""
                ),
                bottomSheetViewState = RecipeListBottomSheetViewState(
                    selectedSearchCriteria = RecipeSearchCriteria.NAME,
                    searchCriteriaList = emptyList(),
                    selectedSortCriteria = RecipeSortCriteria.RELEVANCE,
                    sortCriteriaList = emptyList(),
                    selectedSortOrder = RecipeSortOrder.DESCENDING,
                    sortOrderList = emptyList(),
                    shouldHide = true
                ),
                message = null
            ),
            onRefresh = {},
            onRecipeCardClicked = {},
            onPrefetchItemsAtIndex = {},
            onFunnelTap = {},
            onQueryChange = {},
            onOpenSearch = {},
            onCloseSearch = {},
            onSearchCriteriaSelected = {},
            onSortCriteriaSelected = {},
            onSortOrderSelected = {},
            onClear = {},
            onConfirm = {},
            onDismissBottomSheet = {},
            onMessagePrimaryButtonClicked = {},
            onMessageSecondaryButtonClicked = {}
        )
    }
}

@Preview(name = "Search Spina - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Search Spina - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSearchSpina() {
    CoolRecipesTheme {
        RecipeListScreen(
            viewState = RecipeListScreenViewState(
                recipeList = emptyList(),
                isLoading = false,
                searchBarViewState = RecipeListScreenSearchBarViewState(
                    isSearchActive = true,
                    isFunnelOn = false,
                    isFunnelEnabled = true,
                    isLoading = false,
                    query = "spina"
                ),
                bottomSheetViewState = RecipeListBottomSheetViewState(
                    selectedSearchCriteria = RecipeSearchCriteria.NAME,
                    searchCriteriaList = emptyList(),
                    selectedSortCriteria = RecipeSortCriteria.RELEVANCE,
                    sortCriteriaList = emptyList(),
                    selectedSortOrder = RecipeSortOrder.DESCENDING,
                    sortOrderList = emptyList(),
                    shouldHide = true
                ),
                message = null
            ),
            onRefresh = {},
            onRecipeCardClicked = {},
            onPrefetchItemsAtIndex = {},
            onFunnelTap = {},
            onQueryChange = {},
            onOpenSearch = {},
            onCloseSearch = {},
            onSearchCriteriaSelected = {},
            onSortCriteriaSelected = {},
            onSortOrderSelected = {},
            onClear = {},
            onConfirm = {},
            onDismissBottomSheet = {},
            onMessagePrimaryButtonClicked = {},
            onMessageSecondaryButtonClicked = {}
        )
    }
}

@Preview(name = "Error Message - Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Error Message - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewErrorMessage() {
    CoolRecipesTheme {
        RecipeListScreen(
            viewState = RecipeListScreenViewState(
                recipeList = emptyList(),
                isLoading = false,
                searchBarViewState = RecipeListScreenSearchBarViewState(
                    isSearchActive = false,
                    isFunnelOn = false,
                    isFunnelEnabled = true,
                    isLoading = false,
                    query = ""
                ),
                bottomSheetViewState = RecipeListBottomSheetViewState(
                    selectedSearchCriteria = RecipeSearchCriteria.NAME,
                    searchCriteriaList = emptyList(),
                    selectedSortCriteria = RecipeSortCriteria.RELEVANCE,
                    sortCriteriaList = emptyList(),
                    selectedSortOrder = RecipeSortOrder.DESCENDING,
                    sortOrderList = emptyList(),
                    shouldHide = true
                ),
                message = RecipeListScreenViewState.Message(
                    type = RecipeListScreenViewState.Message.Type.GENERIC
                )
            ),
            onRefresh = {},
            onRecipeCardClicked = {},
            onPrefetchItemsAtIndex = {},
            onFunnelTap = {},
            onQueryChange = {},
            onOpenSearch = {},
            onCloseSearch = {},
            onSearchCriteriaSelected = {},
            onSortCriteriaSelected = {},
            onSortOrderSelected = {},
            onClear = {},
            onConfirm = {},
            onDismissBottomSheet = {},
            onMessagePrimaryButtonClicked = {},
            onMessageSecondaryButtonClicked = {}
        )
    }
}
