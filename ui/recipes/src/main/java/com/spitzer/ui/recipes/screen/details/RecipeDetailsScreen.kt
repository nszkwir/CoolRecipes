package com.spitzer.ui.recipes.screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.spitzer.designsystem.R
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.animations.rememberDuration
import com.spitzer.designsystem.components.LoadingView
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing
import com.spitzer.designsystem.views.message.MessageView
import com.spitzer.ui.recipes.screen.details.views.RecipeDetailsView

@Composable
fun RecipeDetailsScreen(
    modifier: Modifier = Modifier,
    input: RecipeDetailsScreenViewModelInput,
    output: (RecipeDetailsScreenViewModelOutput) -> Unit
) {
    val viewModel =
        hiltViewModel<RecipeDetailsScreenViewModel, RecipeDetailsScreenViewModel.Factory> { factory ->
            factory.create(input)
        }
    viewModel.output = output
    val viewState: RecipeDetailsScreenViewState by viewModel.viewState.collectAsState()

    RecipeDetailsScreen(
        modifier = modifier,
        viewState = viewState,
        onRefresh = viewModel::onRefresh,
        onBackButtonPressed = viewModel::onBackButtonPressed,
        onFavoriteTapped = viewModel::onFavoriteTapped,
        onMessagePrimaryButtonClicked = viewModel::onMessagePrimaryButtonClicked,
        onMessageSecondaryButtonClicked = viewModel::onMessageSecondaryButtonClicked
    )
}

@Composable
fun RecipeDetailsScreen(
    viewState: RecipeDetailsScreenViewState,
    onRefresh: () -> Unit,
    onBackButtonPressed: () -> Unit,
    onFavoriteTapped: () -> Unit,
    onMessagePrimaryButtonClicked: () -> Unit,
    onMessageSecondaryButtonClicked: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val message = viewState.mapMessage(
        onPrimaryButtonClicked = onMessagePrimaryButtonClicked,
        onSecondaryButtonClicked = onMessageSecondaryButtonClicked
    )
    val showLoading = rememberDuration(value = viewState.isLoading, minDurationMillis = 500)

    val state = rememberPullToRefreshState()
    Box(
        modifier = modifier
            .pullToRefresh(
                isRefreshing = false,
                state = state,
                onRefresh = onRefresh
            )
            .fillMaxSize()
            .background(CoolRecipesTheme.colors.n99n00),
        contentAlignment = Alignment.TopStart
    ) {
        when {
            showLoading -> LoadingView()
            message != null -> MessageView(viewState = message)
            else -> {
                if (viewState.recipeDetails != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        RecipeDetailsView(
                            viewState = viewState.recipeDetails,
                            onFavoriteTapped = onFavoriteTapped
                        )

                        IconButton(
                            modifier = Modifier
                                .padding(Spacing.THREE.dp)
                                .align(Alignment.TopStart)
                                .background(
                                    color = CoolRecipesTheme.colors.n99n00,
                                    shape = CircleShape
                                ),
                            onClick = onBackButtonPressed

                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_arrow_back_24),
                                contentDescription = stringResource(id = R.string.back),
                                tint = CoolRecipesTheme.colors.n00n99
                            )
                        }
                    }
                } else {
                    SearchNotFoundView()
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

@Composable
private fun SearchNotFoundView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column {
            LottieAnimationView(
                animation = R.raw.search_not_found,
                modifier = Modifier.height(250.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
