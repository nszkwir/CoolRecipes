package com.spitzer.domain.usecase.favorites

import com.spitzer.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to check whether a specific recipe is currently marked as a favorite.
 *
 * @property repository The [RecipeRepository] providing access to the favorite recipes data.
 */
class GetRecipeFavoriteStatusUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: Long): Flow<Boolean> {
        return repository.favoriteRecipeIds.map {
            it.contains(id)
        }
    }
}
