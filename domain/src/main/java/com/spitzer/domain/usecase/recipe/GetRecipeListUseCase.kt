package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.RecipePage
import com.spitzer.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Use case responsible for retrieving a continuous stream of recipe data.
 *
 * This class interacts with the [RecipeRepository] to provide a [Flow] of [RecipePage],
 * allowing observers to receive updates whenever the recipe list changes.
 *
 * @property repository The repository used to access recipe data sources.
 */
class GetRecipeListUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): StateFlow<RecipePage> {
        return repository.recipePage
    }
}
