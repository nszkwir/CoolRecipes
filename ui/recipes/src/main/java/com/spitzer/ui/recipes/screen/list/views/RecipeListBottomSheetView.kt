package com.spitzer.ui.recipes.screen.list.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.spitzer.designsystem.R
import com.spitzer.designsystem.components.ActionButtonView
import com.spitzer.designsystem.components.ActionButtonViewState
import com.spitzer.designsystem.components.PillViewState
import com.spitzer.designsystem.data.AnnouncedAction
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder

@Immutable
data class RecipeListBottomSheetViewState(
    val selectedSearchCriteria: RecipeSearchCriteria,
    val searchCriteriaList: List<RecipeSearchCriteria>,
    val selectedSortCriteria: RecipeSortCriteria,
    val sortCriteriaList: List<RecipeSortCriteria>,
    val selectedSortOrder: RecipeSortOrder,
    val sortOrderList: List<RecipeSortOrder>,
    val shouldHide: Boolean
)

@Composable
private fun mapSearchCriteriaTitle(recipeSearchCriteria: RecipeSearchCriteria): String {
    return when (recipeSearchCriteria) {
        RecipeSearchCriteria.NAME -> stringResource(R.string.search_criteria_name_title)
        RecipeSearchCriteria.INGREDIENTS -> stringResource(R.string.search_criteria_ingredients_title)
    }
}

@Composable
private fun mapSortCriteriaTitle(sortCriteria: RecipeSortCriteria): String {
    return when (sortCriteria) {
        RecipeSortCriteria.RELEVANCE -> stringResource(R.string.sort_criteria_relevance_title)
        RecipeSortCriteria.POPULARITY -> stringResource(R.string.sort_criteria_popularity_title)
        RecipeSortCriteria.PREPARATION_TIME -> stringResource(R.string.sort_criteria_preparation_time_title)
        RecipeSortCriteria.CALORIES -> stringResource(R.string.sort_criteria_calories_title)
    }
}

@Composable
private fun mapSortOrderTitle(recipeSortOrder: RecipeSortOrder): String {
    return when (recipeSortOrder) {
        RecipeSortOrder.DESCENDING -> stringResource(R.string.sort_order_descending_title)
        RecipeSortOrder.ASCENDING -> stringResource(R.string.sort_order_ascending_title)
    }
}

/**
 * A bottom sheet composable that provides filtering and sorting options for the recipe list.
 *
 * This view allows users to select search criteria (e.g., by name or ingredients),
 * sort criteria (e.g., relevance, popularity), and the sort order (ascending or descending).
 * It also includes actions to clear the selection or confirm the changes.
 *
 * @param viewState The current state of the bottom sheet, including available options and selections.
 * @param onSearchCriteriaSelected Callback triggered when a search criterion is selected.
 * @param onSortCriteriaSelected Callback triggered when a sort criterion is selected.
 * @param onSortOrderSelected Callback triggered when a sort order is selected.
 * @param onDismiss Callback triggered when the bottom sheet is dismissed by the user.
 * @param onClear Callback triggered when the "Clear" action is clicked.
 * @param onConfirm Callback triggered when the "Confirm" action is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListBottomSheetView(
    viewState: RecipeListBottomSheetViewState,
    onSearchCriteriaSelected: (RecipeSearchCriteria) -> Unit,
    onSortCriteriaSelected: (RecipeSortCriteria) -> Unit,
    onSortOrderSelected: (RecipeSortOrder) -> Unit,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit
) {
    val density = LocalDensity.current
    var isHiding by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(viewState.shouldHide) {
        if (viewState.shouldHide) {
            isHiding = true
            sheetState.hide()
        }
    }

    if (sheetState.currentValue == SheetValue.Hidden &&
        sheetState.targetValue == SheetValue.Hidden && isHiding
    ) {
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.FOUR.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.FOUR.dp)
                .padding(bottom = Spacing.FOUR.dp)
        ) {
            FunnelBlockView(
                title = stringResource(R.string.recipe_list_screen_funnel_search_criteria_title),
                pillsViewStates = viewState.searchCriteriaList.map { searchCriteria ->
                    PillViewState(
                        text = mapSearchCriteriaTitle(searchCriteria),
                        isSelected = viewState.selectedSearchCriteria == searchCriteria,
                        onSelectionChange = {
                            onSearchCriteriaSelected(searchCriteria)
                        }
                    )
                }
            )
            FunnelBlockView(
                title = stringResource(R.string.recipe_list_screen_funnel_sort_criteria_title),
                pillsViewStates = viewState.sortCriteriaList.map { sortCriteria ->
                    PillViewState(
                        text = mapSortCriteriaTitle(sortCriteria),
                        isSelected = viewState.selectedSortCriteria == sortCriteria,
                        onSelectionChange = {
                            onSortCriteriaSelected(sortCriteria)
                        }
                    )
                }
            )
            FunnelBlockView(
                title = stringResource(R.string.recipe_list_screen_funnel_sort_order_title),
                pillsViewStates = viewState.sortOrderList.map { sortOrder ->
                    PillViewState(
                        text = mapSortOrderTitle(sortOrder),
                        isSelected = viewState.selectedSortOrder == sortOrder,
                        onSelectionChange = {
                            onSortOrderSelected(sortOrder)
                        }
                    )
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.FOUR.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    Spacing.FOUR.dp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                var buttonMinWidth by remember { mutableStateOf(0.dp) }
                ActionButtonView(
                    modifier = Modifier
                        .onGloballyPositioned {
                            val width = with(density) {
                                it.size.width.toDp()
                            }
                            buttonMinWidth = max(width, buttonMinWidth)
                        }
                        .defaultMinSize(minWidth = buttonMinWidth),
                    viewState = ActionButtonViewState(
                        announcedAction = AnnouncedAction(
                            onClear,
                            stringResource(R.string.clear_title)
                        ),
                        style = ActionButtonViewState.Style.WARNING
                    )
                )

                ActionButtonView(
                    modifier = Modifier
                        .onGloballyPositioned {
                            val width = with(density) {
                                it.size.width.toDp()
                            }
                            buttonMinWidth = max(width, buttonMinWidth)
                        }
                        .defaultMinSize(minWidth = buttonMinWidth),
                    viewState = ActionButtonViewState(
                        announcedAction = AnnouncedAction(
                            onConfirm,
                            stringResource(R.string.confirm_title)
                        )
                    )
                )
            }
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewRecipeListBottomSheetView() {
    CoolRecipesTheme {
        RecipeListBottomSheetView(
            viewState = RecipeListBottomSheetViewState(
                selectedSearchCriteria = RecipeSearchCriteria.NAME,
                searchCriteriaList = RecipeSearchCriteria.entries,
                selectedSortCriteria = RecipeSortCriteria.RELEVANCE,
                sortCriteriaList = RecipeSortCriteria.entries,
                selectedSortOrder = RecipeSortOrder.DESCENDING,
                sortOrderList = RecipeSortOrder.entries,
                shouldHide = false
            ),
            onSearchCriteriaSelected = { },
            onSortCriteriaSelected = {},
            onSortOrderSelected = {},
            onDismiss = {},
            onClear = {},
            onConfirm = {}
        )
    }
}
