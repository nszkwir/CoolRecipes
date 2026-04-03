package com.spitzer.domain.usecase.favorites

import com.spitzer.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRecipeFavoriteStatusUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: Long): Flow<Boolean> {
        return repository.favoriteRecipeIds.map {
            it.contains(id)
        }
    }
}
