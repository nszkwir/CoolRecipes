package com.spitzer.coolrecipes.navigation

import androidx.navigation3.runtime.NavKey
import com.spitzer.ui.coordinator.SettingsCoordinatorRoute
import com.spitzer.ui.coordinator.SettingsScreenRoute
import com.spitzer.ui.recipes.coordinator.FavoritesCoordinatorRoute
import com.spitzer.ui.recipes.coordinator.FavoritesRecipeDetailsScreenRoute
import com.spitzer.ui.recipes.coordinator.FavoritesRecipesListScreenRoute
import com.spitzer.ui.recipes.coordinator.RecipeDetailsScreenRoute
import com.spitzer.ui.recipes.coordinator.RecipesCoordinatorRoute
import com.spitzer.ui.recipes.coordinator.RecipesListScreenRoute
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/**
 * Creates a [SerializersModule] configured for polymorphic serialization of [NavKey] subclasses.
 *
 * This module registers all application-specific navigation routes, including coordinators
 * and individual screens for Recipes, Favorites, and Settings modules. This is required
 * for the navigation system to correctly serialize and deserialize navigation states.
 *
 * @return A configured [SerializersModule] containing all route subclass registrations.
 */
fun getRouteSerializersModule() = SerializersModule {
    polymorphic(NavKey::class) {

        subclass(RecipesCoordinatorRoute::class, RecipesCoordinatorRoute.serializer())
        subclass(RecipesListScreenRoute::class, RecipesListScreenRoute.serializer())
        subclass(RecipeDetailsScreenRoute::class, RecipeDetailsScreenRoute.serializer())

        subclass(FavoritesCoordinatorRoute::class, FavoritesCoordinatorRoute.serializer())
        subclass(
            FavoritesRecipesListScreenRoute::class,
            FavoritesRecipesListScreenRoute.serializer()
        )
        subclass(
            FavoritesRecipeDetailsScreenRoute::class,
            FavoritesRecipeDetailsScreenRoute.serializer()
        )

        subclass(SettingsCoordinatorRoute::class, SettingsCoordinatorRoute.serializer())
        subclass(SettingsScreenRoute::class, SettingsScreenRoute.serializer())
    }
}
