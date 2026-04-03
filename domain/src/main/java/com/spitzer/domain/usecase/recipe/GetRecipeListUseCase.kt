package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.RecipePage
import com.spitzer.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecipeListUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<RecipePage> {
        return repository.recipePage
    }
}
