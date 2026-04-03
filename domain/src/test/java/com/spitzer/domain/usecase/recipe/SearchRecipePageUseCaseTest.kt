package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.recipe.result.SearchRecipeResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URL

class SearchRecipePageUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = SearchRecipePageUseCase(repository)

    @Test
    fun `invoke delegates to repository searchRecipeList with correct params`() = runTest {
        val recipes = listOf(
            Recipe(
                id = 1L,
                title = "Pasta",
                image = URL("https://example.com/pasta.jpg"),
                summary = "Delicious pasta",
                )
        )
        coEvery {
            repository.searchRecipeList(any(), any(), any(), any())
        } returns SearchRecipeResult.Success(recipes)

        val result = useCase(
            query = "pasta",
            searchCriteria = RecipeSearchCriteria.NAME,
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(SearchRecipeResult.Success(recipes), result)
        coVerify {
            repository.searchRecipeList(
                query = "pasta",
                searchCriteria = RecipeSearchCriteria.NAME,
                sortCriteria = RecipeSortCriteria.RELEVANCE,
                sortOrder = RecipeSortOrder.DESCENDING
            )
        }
    }

    @Test
    fun `invoke returns NoInternet when repository returns NoInternet`() = runTest {
        coEvery {
            repository.searchRecipeList(any(), any(), any(), any())
        } returns SearchRecipeResult.NoInternet

        val result = useCase(
            query = "salad",
            searchCriteria = RecipeSearchCriteria.INGREDIENTS,
            sortCriteria = RecipeSortCriteria.POPULARITY,
            sortOrder = RecipeSortOrder.ASCENDING
        )

        assertEquals(SearchRecipeResult.NoInternet, result)
    }

    @Test
    fun `invoke returns Unknown when repository returns Unknown`() = runTest {
        coEvery {
            repository.searchRecipeList(any(), any(), any(), any())
        } returns SearchRecipeResult.Unknown

        val result = useCase(
            query = "cake",
            searchCriteria = RecipeSearchCriteria.NAME,
            sortCriteria = RecipeSortCriteria.CALORIES,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(SearchRecipeResult.Unknown, result)
    }

    @Test
    fun `invoke passes INGREDIENTS search criteria correctly`() = runTest {
        coEvery {
            repository.searchRecipeList(any(), any(), any(), any())
        } returns SearchRecipeResult.Success(emptyList())

        useCase(
            query = "tomato, basil",
            searchCriteria = RecipeSearchCriteria.INGREDIENTS,
            sortCriteria = RecipeSortCriteria.PREPARATION_TIME,
            sortOrder = RecipeSortOrder.ASCENDING
        )

        coVerify {
            repository.searchRecipeList(
                query = "tomato, basil",
                searchCriteria = RecipeSearchCriteria.INGREDIENTS,
                sortCriteria = RecipeSortCriteria.PREPARATION_TIME,
                sortOrder = RecipeSortOrder.ASCENDING
            )
        }
    }
}
