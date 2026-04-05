package com.spitzer.domain.usecase.favorites

import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case responsible for retrieving the list of recipes marked as favorites by the user.
 *
 * This use case interacts with the repository layer to fetch all recipes that have been
 * saved or bookmarked, allowing the UI to display the user's personalized collection.
 */
class GetFavoriteRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> {
        return repository.favoriteRecipes
    }
}