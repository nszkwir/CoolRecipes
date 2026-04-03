package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> {
        return repository.favoriteRecipes
    }
}
