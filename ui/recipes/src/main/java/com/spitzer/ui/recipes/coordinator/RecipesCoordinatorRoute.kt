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
import com.spitzer.ui.recipes.screen.list.RecipeListScreen
import com.spitzer.ui.recipes.screen.list.RecipeListScreenViewModelOutput
import kotlinx.serialization.Serializable

@Serializable
data object RecipesCoordinatorRoute : NavKey

@Serializable
data object RecipesListScreenRoute : NavKey

@Serializable
data class RecipeDetailsScreenRoute(val recipeId: Long) : NavKey


@Composable
fun RecipesCoordinator(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        elements = arrayOf<NavKey>(RecipesListScreenRoute)
    )
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<RecipesListScreenRoute> {
                RecipeListScreen(
                    output = { output ->
                        when (output) {
                            is RecipeListScreenViewModelOutput.RecipeDetail -> {
                                backStack.add(RecipeDetailsScreenRoute(output.recipeId))
                            }
                        }
                    }
                )
            }

            entry<RecipeDetailsScreenRoute> {
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
