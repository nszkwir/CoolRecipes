package com.spitzer.ui.coordinator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.spitzer.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsCoordinatorRoute : NavKey

@Serializable
data object SettingsScreenRoute : NavKey

@Composable
fun SettingsCoordinator(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        elements = arrayOf<NavKey>(SettingsScreenRoute)
    )
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<SettingsScreenRoute> {
                SettingsScreen()
            }
        }
    )
}
