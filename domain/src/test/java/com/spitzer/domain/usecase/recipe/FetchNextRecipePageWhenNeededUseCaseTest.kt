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

class FetchNextRecipePageWhenNeededUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = FetchNextRecipePageWhenNeededUseCase(repository)

    @Test
    fun `invoke delegates to repository fetchRecipeList with correct params`() = runTest {
        coEvery {
            repository.fetchRecipeList(any(), any(), any())
        } returns RecipePaginationResult.Success

        val result = useCase(
            elementIndex = 15,
            sortCriteria = RecipeSortCriteria.POPULARITY,
            sortOrder = RecipeSortOrder.ASCENDING
        )

        assertEquals(RecipePaginationResult.Success, result)
        coVerify {
            repository.fetchRecipeList(
                elementIndex = 15,
                sortCriteria = RecipeSortCriteria.POPULARITY,
                sortOrder = RecipeSortOrder.ASCENDING
            )
        }
    }

    @Test
    fun `invoke returns WrongIndex when repository returns WrongIndex`() = runTest {
        coEvery {
            repository.fetchRecipeList(any(), any(), any())
        } returns RecipePaginationResult.WrongIndex

        val result = useCase(
            elementIndex = 0,
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(RecipePaginationResult.WrongIndex, result)
    }

    @Test
    fun `invoke returns NoInternet when repository returns NoInternet`() = runTest {
        coEvery {
            repository.fetchRecipeList(any(), any(), any())
        } returns RecipePaginationResult.NoInternet

        val result = useCase(
            elementIndex = 5,
            sortCriteria = RecipeSortCriteria.CALORIES,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(RecipePaginationResult.NoInternet, result)
    }

    @Test
    fun `invoke passes elementIndex correctly`() = runTest {
        coEvery {
            repository.fetchRecipeList(any(), any(), any())
        } returns RecipePaginationResult.Success

        useCase(
            elementIndex = 42,
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        coVerify {
            repository.fetchRecipeList(
                elementIndex = 42,
                sortCriteria = RecipeSortCriteria.RELEVANCE,
                sortOrder = RecipeSortOrder.DESCENDING
            )
        }
    }
}
