package com.spitzer.ui.recipes.coordinator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.spitzer.ui.recipes.screen.details.RecipeDetailsScreen
import com.spitzer.ui.recipes.screen.details.RecipeDetailsScreenViewModelInput
import com.spitzer.ui.recipes.screen.details.RecipeDetailsScreenViewModelOutput
import com.spitzer.ui.recipes.screen.favorites.FavoriteRecipesScreen
import com.spitzer.ui.recipes.screen.favorites.FavoriteRecipesViewModelOutput
import kotlinx.serialization.Serializable

@Serializable
data object FavoritesCoordinatorRoute : NavKey

@Serializable
data object FavoritesRecipesListScreenRoute : NavKey

@Serializable
data class FavoritesRecipeDetailsScreenRoute(val recipeId: Long) : NavKey


@Composable
fun FavoritesCoordinator(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        elements = arrayOf<NavKey>(FavoritesRecipesListScreenRoute)
    )
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<FavoritesRecipesListScreenRoute> {
                FavoriteRecipesScreen(
                    output = { output ->
                        when (output) {
                            is FavoriteRecipesViewModelOutput.RecipeDetail -> {
                                backStack.add(FavoritesRecipeDetailsScreenRoute(output.recipeId))
                            }
                        }
                    }
                )
            }

            entry<FavoritesRecipeDetailsScreenRoute> {
                RecipeDetailsScreen(
                    input = RecipeDetailsScreenViewModelInput(recipeId = it.recipeId),
                    output = { output ->
                        when (output) {
                            is RecipeDetailsScreenViewModelOutput.ScreenNavigateBack -> {
                                backStack.removeLastOrNull()
                            }
                        }
                    }
                )
            }
        }
    )
}
