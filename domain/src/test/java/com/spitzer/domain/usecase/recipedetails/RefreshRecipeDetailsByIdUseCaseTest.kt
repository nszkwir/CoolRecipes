package com.spitzer.domain.usecase.recipedetails

import com.spitzer.domain.model.recipe.RecipeDetails
import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.recipedetails.result.RecipeDetailsResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URL

class RefreshRecipeDetailsByIdUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = RefreshRecipeDetailsByIdUseCase(repository)

    private val sampleRecipeDetails = RecipeDetails(
        id = 5L,
        title = "Fresh Recipe",
        readyInMinutes = 20,
        servings = 2,
        summary = "A refreshed recipe",
        instructions = "Mix and serve",
        vegetarian = true,
        vegan = true,
        glutenFree = true,
        dairyFree = true,
        image = URL("https://example.com/fresh.jpg"),
        healthScore = 90.0,
        diets = listOf("vegan", "gluten free"),
        spoonacularScore = 92.0,
        spoonacularSourceUrl = URL("https://spoonacular.com/recipe-5"),
        ingredients = listOf("lettuce", "tomato")
    )

    @Test
    fun `invoke delegates to repository fetchRecipeDetails`() = runTest {
        coEvery { repository.fetchRecipeDetails(5L) } returns
                RecipeDetailsResult.Success(sampleRecipeDetails)

        val result = useCase(5L)

        assertEquals(RecipeDetailsResult.Success(sampleRecipeDetails), result)
        coVerify { repository.fetchRecipeDetails(id = 5L) }
    }

    @Test
    fun `invoke returns NoInternet when repository returns NoInternet`() = runTest {
        coEvery { repository.fetchRecipeDetails(any()) } returns RecipeDetailsResult.NoInternet

        val result = useCase(10L)

        assertEquals(RecipeDetailsResult.NoInternet, result)
    }

    @Test
    fun `invoke returns Unknown when repository returns Unknown`() = runTest {
        coEvery { repository.fetchRecipeDetails(any()) } returns RecipeDetailsResult.Unknown

        val result = useCase(15L)

        assertEquals(RecipeDetailsResult.Unknown, result)
    }

    @Test
    fun `invoke passes correct id to repository`() = runTest {
        coEvery { repository.fetchRecipeDetails(any()) } returns RecipeDetailsResult.Unknown

        useCase(333L)

        coVerify { repository.fetchRecipeDetails(id = 333L) }
    }
}
