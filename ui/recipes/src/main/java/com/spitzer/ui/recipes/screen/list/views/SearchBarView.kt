package com.spitzer.ui.recipes.screen.list.views

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.R
import com.spitzer.designsystem.R.raw.funnel_off
import com.spitzer.designsystem.R.raw.funnel_on
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.components.LoadingView
import com.spitzer.designsystem.theme.BorderRadius
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing
import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Immutable
data class RecipeListScreenSearchBarViewState(
    val isSearchActive: Boolean,
    val isFunnelOn: Boolean,
    val searchCriteria: RecipeSearchCriteria = RecipeSearchCriteria.NAME,
    val isLoading: Boolean,
    val query: String,
    val recipesList: List<Recipe> = emptyList(),
    val isFunnelEnabled: Boolean
)

private data class RectInfo(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

/**
 * A composable that displays a search bar with an expanding container transformation for the recipe list.
 *
 * When activated, the search bar expands to fill the screen and displays a list of search results
 * based on the current [viewState]. It handles different states such as loading, empty results,
 * and populated recipe lists, while providing a funnel/filter option.
 *
 * @param modifier The [Modifier] to be applied to the search field.
 * @param viewState The current state of the search bar, including query, results, and active status.
 * @param onRecipeCardClicked Callback invoked when a recipe from the result list is tapped.
 * @param onFunnelTap Optional callback for when the filter/funnel icon is clicked.
 * @param onQueryChange Callback invoked when the user types in the search field.
 * @param onOpenSearch Callback invoked when the search field is activated (clicked to expand).
 * @param onCloseSearch Callback invoked when the search is dismissed or closed.
 */
@Composable
fun RecipeListScreenSearchBarView(
    modifier: Modifier = Modifier,
    viewState: RecipeListScreenSearchBarViewState,
    onRecipeCardClicked: (Recipe) -> Unit,
    onFunnelTap: (() -> Unit)? = null,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit
) {
    var searchFieldRect by remember { mutableStateOf<RectInfo?>(null) }
    var parentWindowOffset by remember { mutableStateOf(Offset.Zero) }
    var showResults by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val config = LocalConfiguration.current

    val searchBarTotalHeight = 88.dp // 16dp padding + 56dp field + 16dp gap

    // Dismiss search on back press / Escape
    BackHandler(enabled = viewState.isSearchActive) {
        onCloseSearch()
    }

    // Delay results appearance
    LaunchedEffect(viewState.isSearchActive) {
        if (viewState.isSearchActive) {
            delay(150)
            showResults = true
        } else {
            showResults = false
        }
    }

    Box(
        Modifier.onGloballyPositioned { coords ->
            parentWindowOffset = coords.localToWindow(Offset.Zero)
        }
    ) {
        // Expanding/Collapse background animation for Search Field
        searchFieldRect?.let { startRect ->
            val transition = updateTransition(
                targetState = viewState.isSearchActive,
                label = "containerTransform"
            )

            val x by transition.animateFloat({ tween(500) }) { active -> if (active) 0f else startRect.x }
            val y by transition.animateFloat({ tween(500) }) { active -> if (active) 0f else startRect.y }
            val width by transition.animateFloat({ tween(500) }) { active ->
                if (active) with(density) { config.screenWidthDp.dp.toPx() } else startRect.width
            }
            val height by transition.animateFloat({ tween(500) }) { active ->
                if (active) with(density) { config.screenHeightDp.dp.toPx() } else startRect.height
            }
            val corner by transition.animateDp({ tween(500) }) { active -> if (active) 0.dp else BorderRadius.SEVEN.dp }

            Box(
                Modifier
                    .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                    .size(with(density) { width.toDp() }, with(density) { height.toDp() })
                    .clip(RoundedCornerShape(corner))
                    .background(CoolRecipesTheme.colors.n80n20)
            ) {
                // Search Results inside colored area
                AnimatedVisibility(
                    visible = showResults,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(150))
                ) {
                    when {
                        viewState.isLoading -> {
                            LoadingView()
                        }

                        viewState.recipesList.isEmpty() -> {
                            EmptySearchView(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = searchBarTotalHeight)
                            )
                        }

                        else -> {
                            RecipesCardListView(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        top = searchBarTotalHeight,
                                        start = Spacing.FOUR.dp,
                                        end = Spacing.FOUR.dp
                                    ),
                                cardListViewStates = mapCardListViewStates(
                                    recipeList = viewState.recipesList,
                                    onCardClicked = onRecipeCardClicked
                                ),
                                onPrefetchItemsAtIndex = null
                            )
                        }

                    }
                }
            }
        }

        // Search Field
        // Always on top, drawn last
        RecipeSearchField(
            modifier = modifier.padding(vertical = Spacing.FOUR.dp),
            hint = when (viewState.searchCriteria) {
                RecipeSearchCriteria.NAME -> stringResource(R.string.recipe_list_screen_search_by_name_placeholder)
                RecipeSearchCriteria.INGREDIENTS -> stringResource(R.string.recipe_list_screen_search_by_ingredients_placeholder)
            },
            query = viewState.query,
            isActive = viewState.isSearchActive,
            isFunnelOn = viewState.isFunnelOn,
            isFunnelEnabled = viewState.isFunnelEnabled,
            onActivate = onOpenSearch,
            onDeactivate = onCloseSearch,
            onQueryChange = onQueryChange,
            onFunnelTap = onFunnelTap,
            onPositioned = { windowRect ->
                searchFieldRect = RectInfo(
                    x = windowRect.x - parentWindowOffset.x,
                    y = windowRect.y - parentWindowOffset.y,
                    width = windowRect.width,
                    height = windowRect.height
                )
            }
        )
    }
}

/**
 * A private internal composable that renders the visual search input field.
 *
 * This component manages the text input state, focus requests when activated,
 * and handles the display of leading search/close icons and the optional filter (funnel) icon.
 * It also reports its layout coordinates via [onPositioned] to support the container
 * transform animation in the parent view.
 *
 * @param modifier The [Modifier] to be applied to the search field container.
 * @param query The current text value typed into the search field.
 * @param hint The placeholder text to display when the field is empty and inactive.
 * @param isActive Whether the search field is currently in its expanded/active state.
 * @param isFunnelOn Whether the filter/funnel is currently active (used to toggle the animation).
 * @param isFunnelEnabled Whether the filter/funnel icon should be visible at all.
 * @param onQueryChange Callback invoked when the user modifies the text in the [BasicTextField].
 * @param onActivate Callback invoked when the user taps the field to begin searching.
 * @param onDeactivate Callback invoked when the user taps the close icon or field to stop searching.
 * @param onFunnelTap Optional callback invoked when the filter/funnel icon is clicked.
 * @param onPositioned Callback that returns the [RectInfo] (position and size) of this field
 * in window coordinates.
 */
@Composable
private fun RecipeSearchField(
    modifier: Modifier = Modifier,
    query: String,
    hint: String,
    isActive: Boolean,
    isFunnelOn: Boolean,
    isFunnelEnabled: Boolean,
    onQueryChange: (String) -> Unit,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    onFunnelTap: (() -> Unit)? = null,
    onPositioned: (RectInfo) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isActive) {
        if (isActive) focusRequester.requestFocus()
    }

    val iconAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.7f,
        animationSpec = tween(300),
        label = "iconFade"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(CoolRecipesTheme.colors.n80n20, RoundedCornerShape(BorderRadius.EIGHT.dp))
            .onGloballyPositioned { layoutCoordinates ->
                val pos = layoutCoordinates.localToWindow(Offset.Zero)
                onPositioned(
                    RectInfo(
                        x = pos.x,
                        y = pos.y,
                        width = layoutCoordinates.size.width.toFloat(),
                        height = layoutCoordinates.size.height.toFloat()
                    )
                )
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { if (isActive) onDeactivate() else onActivate() }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Spacing.FOUR.dp)
        ) {
            // Leading icon: search or close
            Image(
                painter = if (isActive) painterResource(R.drawable.close) else painterResource(R.drawable.search),
                contentDescription = if (isActive) {
                    stringResource(R.string.recipe_list_screen_search_close_content_description)
                } else {
                    stringResource(R.string.recipe_list_screen_search_content_description)
                },
                modifier = Modifier.alpha(iconAlpha)
            )

            Spacer(Modifier.width(Spacing.FOUR.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty() && !isActive) {
                    Text(
                        text = hint,
                        color = CoolRecipesTheme.colors.n00n99.copy(alpha = 0.5f),
                        style = CoolRecipesTheme.typography.body1
                    )
                }
                if (isActive)
                    BasicTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = CoolRecipesTheme.typography.body1.copy(
                            color = CoolRecipesTheme.colors.n00n99
                        ),
                        cursorBrush = SolidColor(CoolRecipesTheme.colors.n00n99),
                    )
            }

            // Showing funnel icon if enabled
            if (isFunnelEnabled) {
                onFunnelTap?.let {
                    Spacer(Modifier.width(Spacing.THREE.dp))
                    LottieAnimationView(
                        modifier = Modifier
                            .size(width = 48.dp, height = 48.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onFunnelTap() }
                            ),
                        animation = if (isFunnelOn) funnel_on else funnel_off,
                        shouldLoop = true,
                    )
                }
            }
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewSearchBarView() {
    CoolRecipesTheme {
        Column {
            RecipeListScreenSearchBarView(
                modifier = Modifier.padding(Spacing.FIVE.dp),
                viewState = RecipeListScreenSearchBarViewState(
                    isSearchActive = false,
                    searchCriteria = RecipeSearchCriteria.NAME,
                    isLoading = false,
                    isFunnelOn = true,
                    isFunnelEnabled = true,
                    query = "",
                    recipesList = listOf()
                ),
                onRecipeCardClicked = {},
                onFunnelTap = { },
                onQueryChange = { },
                onOpenSearch = { },
                onCloseSearch = { }
            )
            RecipeListScreenSearchBarView(
                modifier = Modifier.padding(Spacing.FIVE.dp),
                viewState = RecipeListScreenSearchBarViewState(
                    isSearchActive = false,
                    searchCriteria = RecipeSearchCriteria.INGREDIENTS,
                    isLoading = false,
                    isFunnelOn = false,
                    isFunnelEnabled = false,
                    query = "",
                    recipesList = listOf()
                ),
                onRecipeCardClicked = {},
                onFunnelTap = null,
                onQueryChange = { },
                onOpenSearch = { },
                onCloseSearch = { }
            )
        }
    }
}
