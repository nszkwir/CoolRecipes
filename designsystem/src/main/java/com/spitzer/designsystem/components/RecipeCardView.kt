package com.spitzer.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.spitzer.designsystem.R
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.data.AnnouncedAction
import com.spitzer.designsystem.extensions.shimmerEffect
import com.spitzer.designsystem.theme.BorderRadius
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing
import java.net.URL

sealed interface RecipeCardViewState {
    data object Loading : RecipeCardViewState
    data class Content(
        val topImageURL: URL? = null,
        val iconButtonURL: URL? = null,
        val firstTitle: String? = null,
        val secondTitle: String? = null,
        val onTap: AnnouncedAction? = null,
        val onIconButtonTapped: AnnouncedAction? = null
    ) : RecipeCardViewState
}

/**
 * A composable that displays a recipe in a card format, supporting both a content state
 * and a loading shimmer state.
 *
 * In the [RecipeCardViewState.Content] state, it displays a top image (or a placeholder animation),
 * a primary title, and a secondary title which supports HTML formatting. The entire card
 * is clickable based on the provided [RecipeCardViewState.Content.onTap] action.
 *
 * @param modifier The modifier to be applied to the card container.
 * @param viewState The state representing whether to show the recipe details or a loading placeholder.
 */
@Composable
fun RecipeCardView(
    modifier: Modifier = Modifier,
    viewState: RecipeCardViewState
) {
    when (viewState) {
        is RecipeCardViewState.Content -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.TWO.dp)
                    .clickable { viewState.onTap?.action?.invoke() },
                shape = RoundedCornerShape(BorderRadius.FOUR.dp),
                border = BorderStroke(1.dp, CoolRecipesTheme.colors.n80n80),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CoolRecipesTheme.colors.n99n99
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TopSectionView(
                        viewState = viewState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CoolRecipesTheme.colors.p00p00)
                            .aspectRatio(104f / 77f)
                    )
                    BottomSectionView(
                        viewState = viewState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        is RecipeCardViewState.Loading -> {
            LoadingCardView(modifier = modifier)
        }
    }
}

@Composable
private fun TopSectionView(
    modifier: Modifier = Modifier,
    viewState: RecipeCardViewState.Content
) {
    viewState.topImageURL?.let {
        AsyncImage(
            modifier = modifier,
            model = it.toString(),
            // Providing a meaningful description based on the title for screen readers
            contentDescription = viewState.firstTitle
                ?: stringResource(R.string.recipe_card_view_recipe_image),
            // Crop ensures the image fills the space cleanly without distortion
            contentScale = ContentScale.Crop
        )
    } ?: run {
        LottieAnimationView(
            modifier = modifier,
            animation = R.raw.empty_search
        )
    }
}

@Composable
private fun BottomSectionView(
    modifier: Modifier = Modifier,
    viewState: RecipeCardViewState.Content
) {
    Column(
        modifier = modifier
            .background(CoolRecipesTheme.colors.n80n20)
            .padding(Spacing.FOUR.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.THREE.dp)
    ) {
        viewState.firstTitle?.let { text ->
            Text(
                text = text,
                color = CoolRecipesTheme.colors.n20n80,
                style = CoolRecipesTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        viewState.secondTitle?.let { text ->
            HTMLTextView(
                text = text,
                color = CoolRecipesTheme.colors.n20n80,
                style = CoolRecipesTheme.typography.body2,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun LoadingCardView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BorderRadius.FOUR.dp))
            .background(CoolRecipesTheme.colors.p00p00)
            .shimmerEffect()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(104f / 77f)
                .background(Color.LightGray)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Transparent)
        )
    }
}


@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewRecipeCardView() {
    CoolRecipesTheme {
        Surface(color = CoolRecipesTheme.colors.n99n00) {
            Column(
                modifier = Modifier
                    .padding(Spacing.FOUR.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.FOUR.dp)
            ) {
                RecipeCardView(viewState = RecipeCardViewState.Loading)
                RecipeCardView(
                    viewState = RecipeCardViewState.Content(
                        topImageURL = URL("https://img.spoonacular.com/recipes/654959-312x231.jpg"),
                        firstTitle = "Pasta On The Border",
                        secondTitle = "Need a <b>diary free main course</b>? Pastan On The Border could be an outstanding recipe to try."
                    )
                )
                RecipeCardView(
                    viewState = RecipeCardViewState.Content(
                        firstTitle = "Pasta On The Border",
                        secondTitle = "Need a <b>diary free main course</b>? Pastan On The Border could be an outstanding recipe to try."
                    )
                )
            }
        }
    }
}
