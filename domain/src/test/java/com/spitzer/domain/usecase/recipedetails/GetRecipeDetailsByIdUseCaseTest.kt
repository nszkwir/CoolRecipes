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

class GetRecipeDetailsByIdUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = GetRecipeDetailsByIdUseCase(repository)

    private val sampleRecipeDetails = RecipeDetails(
        id = 1L,
        title = "Test Recipe",
        readyInMinutes = 30,
        servings = 4,
        summary = "A test recipe",
        instructions = "Cook it",
        vegetarian = false,
        vegan = false,
        glutenFree = true,
        dairyFree = false,
        image = URL("https://example.com/recipe.jpg"),
        healthScore = 75.0,
        diets = listOf("gluten free"),
        spoonacularScore = 85.0,
        spoonacularSourceUrl = URL("https://spoonacular.com/recipe-1"),
        ingredients = listOf("flour", "sugar", "eggs")
    )

    @Test
    fun `invoke returns Success when repository returns recipe details`() = runTest {
        coEvery { repository.getRecipeDetailsById(1L) } returns
                RecipeDetailsResult.Success(sampleRecipeDetails)

        val result = useCase(1L)

        assertEquals(RecipeDetailsResult.Success(sampleRecipeDetails), result)
        coVerify { repository.getRecipeDetailsById(id = 1L) }
    }

    @Test
    fun `invoke returns NoInternet when repository returns NoInternet`() = runTest {
        coEvery { repository.getRecipeDetailsById(any()) } returns RecipeDetailsResult.NoInternet

        val result = useCase(99L)

        assertEquals(RecipeDetailsResult.NoInternet, result)
    }

    @Test
    fun `invoke returns Unknown when repository returns Unknown`() = runTest {
        coEvery { repository.getRecipeDetailsById(any()) } returns RecipeDetailsResult.Unknown

        val result = useCase(42L)

        assertEquals(RecipeDetailsResult.Unknown, result)
    }

    @Test
    fun `invoke passes correct id to repository`() = runTest {
        coEvery { repository.getRecipeDetailsById(any()) } returns RecipeDetailsResult.Unknown

        useCase(777L)

        coVerify { repository.getRecipeDetailsById(id = 777L) }
    }
}
