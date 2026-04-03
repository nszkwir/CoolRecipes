package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.repository.RecipeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL

class GetFavoriteRecipesUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = GetFavoriteRecipesUseCase(repository)

    @Test
    fun `invoke returns favoriteRecipes flow from repository`() = runTest {
        val recipes = listOf(
            Recipe(
                id = 1L,
                title = "Pizza",
                image = URL("https://example.com/pizza.jpg"),
                summary = "Great pizza",
                )
        )
        val flow = MutableStateFlow(recipes)
        every { repository.favoriteRecipes } returns flow

        val result = useCase().first()

        assertEquals(recipes, result)
        verify { repository.favoriteRecipes }
    }

    @Test
    fun `invoke returns empty list when no favorites`() = runTest {
        val flow = MutableStateFlow(emptyList<Recipe>())
        every { repository.favoriteRecipes } returns flow

        val result = useCase().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke emits updated values when repository updates`() = runTest {
        val initial = emptyList<Recipe>()
        val updated = listOf(
            Recipe(
                id = 2L,
                title = "Soup",
                image = null,
                summary = "Warm soup",
                )
        )
        val flow = MutableStateFlow(initial)
        every { repository.favoriteRecipes } returns flow

        assertEquals(initial, useCase().first())

        flow.value = updated
        assertEquals(updated, useCase().first())
    }
}
