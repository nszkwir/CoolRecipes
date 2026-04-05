package com.spitzer.coolrecipes.appBar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.spitzer.designsystem.R
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing

data class TopNavItem(
    @field:DrawableRes val icon: Int,
    @field:StringRes val name: Int,
    val onClick: () -> Unit
)

data class MyTopAppBarViewState(
    val title: String,
    val leftNavItem: TopNavItem? = null,
    val rightNavItem: TopNavItem? = null
)


/**
 * A custom centered top app bar component for the CoolRecipes application.
 *
 * This composable wraps a [CenterAlignedTopAppBar] and configures it using the provided [viewState],
 * supporting a centered title and optional navigation or action icons on the left and right.
 *
 * @param modifier The [Modifier] to be applied to this top app bar.
 * @param viewState The [MyTopAppBarViewState] containing the configuration for the title and navigation items.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    modifier: Modifier = Modifier,
    viewState: MyTopAppBarViewState
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            viewState.leftNavItem?.let {
                IconButton(onClick = {
                    viewState.leftNavItem.onClick()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = viewState.leftNavItem.icon),
                        contentDescription = stringResource(viewState.leftNavItem.name),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(bottom = Spacing.TWO.dp)
                            .height(25.dp),
                        tint = CoolRecipesTheme.colors.n00n99
                    )
                }
            }
        },
        actions = {
            viewState.rightNavItem?.let {
                IconButton(onClick = {
                    viewState.rightNavItem.onClick()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = viewState.rightNavItem.icon),
                        contentDescription = stringResource(viewState.rightNavItem.name),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(bottom = Spacing.TWO.dp)
                            .height(25.dp),
                        tint = CoolRecipesTheme.colors.n00n99
                    )
                }
            }
        },
        title = {
            Text(
                text = viewState.title,
                color = CoolRecipesTheme.colors.n00n99,
                style = CoolRecipesTheme.typography.heading2,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CoolRecipesTheme.colors.n99n00
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun MyTopAppBarPreview() {
    CoolRecipesTheme {
        MyTopAppBar(
            viewState = MyTopAppBarViewState(
                title = "Cool Recipes",
                leftNavItem = TopNavItem(
                    icon = R.drawable.baseline_home_24,
                    name = R.string.recipe_list_screen_title,
                    onClick = {}
                ),
                rightNavItem = TopNavItem(
                    icon = R.drawable.baseline_settings_24,
                    name = R.string.Settings,
                    onClick = {}
                )
            )
        )
    }
}
