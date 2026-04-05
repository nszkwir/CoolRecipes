package com.spitzer.domain.usecase.favorites

import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.favorites.result.SetRecipeFavoriteStatusResult
import javax.inject.Inject

/**
 * Use case responsible for updating the favorite status of a specific recipe.
 *
 * This use case interacts with the [RecipeRepository] to persist or remove a recipe
 * from the user's favorites list.
 *
 * @property repository The repository handling recipe data operations.
 */
class SetRecipeFavoriteStatusUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        id: Long,
        isFavorite: Boolean,
        title: String,
        image: String?,
        summary: String
    ): SetRecipeFavoriteStatusResult {
        return repository.setRecipeFavorite(id, isFavorite, title, image, summary)
    }
}
