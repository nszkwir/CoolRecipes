package com.spitzer.domain.usecase.favorites

import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.favorites.result.SetRecipeFavoriteStatusResult
import javax.inject.Inject

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
