package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.recipe.result.RecipePaginationResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RefreshRecipeListUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = RefreshRecipeListUseCase(repository)

    @Test
    fun `invoke delegates to repository refreshRecipeList with correct params`() = runTest {
        coEvery {
            repository.refreshRecipeList(any(), any())
        } returns RecipePaginationResult.Success

        val result = useCase(
            sortCriteria = RecipeSortCriteria.POPULARITY,
            sortOrder = RecipeSortOrder.ASCENDING
        )

        assertEquals(RecipePaginationResult.Success, result)
        coVerify {
            repository.refreshRecipeList(
                sortCriteria = RecipeSortCriteria.POPULARITY,
                sortOrder = RecipeSortOrder.ASCENDING
            )
        }
    }

    @Test
    fun `invoke returns NoInternet when repository returns NoInternet`() = runTest {
        coEvery {
            repository.refreshRecipeList(any(), any())
        } returns RecipePaginationResult.NoInternet

        val result = useCase(
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(RecipePaginationResult.NoInternet, result)
    }

    @Test
    fun `invoke returns Empty when repository returns Empty`() = runTest {
        coEvery {
            repository.refreshRecipeList(any(), any())
        } returns RecipePaginationResult.Empty

        val result = useCase(
            sortCriteria = RecipeSortCriteria.CALORIES,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(RecipePaginationResult.Empty, result)
    }

    @Test
    fun `invoke returns Unknown when repository returns Unknown`() = runTest {
        coEvery {
            repository.refreshRecipeList(any(), any())
        } returns RecipePaginationResult.Unknown

        val result = useCase(
            sortCriteria = RecipeSortCriteria.PREPARATION_TIME,
            sortOrder = RecipeSortOrder.ASCENDING
        )

        assertEquals(RecipePaginationResult.Unknown, result)
    }
}
