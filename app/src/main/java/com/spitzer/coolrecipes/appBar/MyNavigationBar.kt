package com.spitzer.coolrecipes.appBar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.spitzer.designsystem.R
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing
import com.spitzer.ui.coordinator.SettingsCoordinatorRoute
import com.spitzer.ui.recipes.coordinator.FavoritesCoordinatorRoute
import com.spitzer.ui.recipes.coordinator.RecipesCoordinatorRoute

data class BottomNavItem(
    @field:DrawableRes val icon: Int,
    @field:StringRes val title: Int
)

val bottomNavItemsDefault = mapOf<NavKey, BottomNavItem>(
    RecipesCoordinatorRoute to BottomNavItem(
        R.drawable.baseline_home_24,
        R.string.recipe_list_screen_title
    ),
    FavoritesCoordinatorRoute to BottomNavItem(R.drawable.baseline_favorite_24, R.string.Favorites),
    SettingsCoordinatorRoute to BottomNavItem(R.drawable.baseline_settings_24, R.string.Settings),
)

@Composable
fun MyNavigationBar(
    modifier: Modifier = Modifier,
    selectedKey: NavKey,
    onItemSelected: (NavKey) -> Unit,
    items: Map<NavKey, BottomNavItem> = bottomNavItemsDefault,
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = CoolRecipesTheme.colors.n99n00
    ) {
        items.forEach { item ->
            val icon = ImageVector.vectorResource(id = item.value.icon)
            val label = stringResource(id = item.value.title)
            val isSelected = item.key == selectedKey
            NavigationBarItem(
                modifier = Modifier,
                selected = isSelected,
                onClick = { if (item != selectedKey) onItemSelected(item.key) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(bottom = Spacing.TWO.dp)
                            .height(25.dp),
                        tint = if (isSelected) CoolRecipesTheme.colors.p00p00 else CoolRecipesTheme.colors.n00n99
                    )
                },
                label = {
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSelected) CoolRecipesTheme.colors.p00p00 else CoolRecipesTheme.colors.n00n99,
                        style = CoolRecipesTheme.typography.caption1,
                        fontWeight = FontWeight.Light
                    )
                }
            )
        }
    }
}
