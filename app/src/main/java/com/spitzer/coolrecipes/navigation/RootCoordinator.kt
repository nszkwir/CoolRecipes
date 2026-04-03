package com.spitzer.coolrecipes.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.spitzer.coolrecipes.appBar.MyNavigationBar
import com.spitzer.coolrecipes.appBar.MyTopAppBar
import com.spitzer.coolrecipes.appBar.MyTopAppBarViewState
import com.spitzer.coolrecipes.appBar.bottomNavItemsDefault
import com.spitzer.coolrecipes.core.Navigator
import com.spitzer.coolrecipes.core.rememberNavigationState
import com.spitzer.coolrecipes.core.toEntries
import com.spitzer.ui.coordinator.SettingsCoordinator
import com.spitzer.ui.coordinator.SettingsCoordinatorRoute
import com.spitzer.ui.recipes.coordinator.FavoritesCoordinator
import com.spitzer.ui.recipes.coordinator.FavoritesCoordinatorRoute
import com.spitzer.ui.recipes.coordinator.RecipesCoordinator
import com.spitzer.ui.recipes.coordinator.RecipesCoordinatorRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootCoordinator(
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(
        startRoute = RecipesCoordinatorRoute,
        topLevelRoutes = bottomNavItemsDefault.keys
    )
    val navigator = remember {
        Navigator(navigationState)
    }

    var topBarConfig: MyTopAppBarViewState? by remember {
        mutableStateOf(null)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            topBarConfig?.let {
                MyTopAppBar(viewState = it)
            }
        },
        bottomBar = {
            MyNavigationBar(
                selectedKey = navigationState.topLevelRoute,
                onItemSelected = {
                    navigator.navigate(it)
                },
                items = bottomNavItemsDefault
            )
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = navigator::goBack,
            entries = navigationState.toEntries(
                entryProvider {
                    entry<RecipesCoordinatorRoute> {
                        topBarConfig = MyTopAppBarViewState(
                            title = "Recipes"
                        )
                        RecipesCoordinator()
                    }

                    entry<FavoritesCoordinatorRoute> {
                        topBarConfig = MyTopAppBarViewState(
                            title = "Favorites"
                        )
                        FavoritesCoordinator()
                    }

                    entry<SettingsCoordinatorRoute> {
                        topBarConfig = MyTopAppBarViewState(
                            title = "Settings"
                        )
                        SettingsCoordinator()
                    }
                }
            )
        )
    }
}
