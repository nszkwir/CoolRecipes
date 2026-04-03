package com.spitzer.domain.usecase.recipe

import com.spitzer.domain.model.recipe.RecipePage
import com.spitzer.domain.repository.RecipeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRecipeListUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = GetRecipeListUseCase(repository)

    @Test
    fun `invoke returns recipePage flow from repository`() = runTest {
        val expected = RecipePage(list = emptyList(), totalResults = 0)
        val stateFlow = MutableStateFlow(expected)
        every { repository.recipePage } returns stateFlow

        val result = useCase().first()

        assertEquals(expected, result)
        verify { repository.recipePage }
    }

    @Test
    fun `invoke emits updated values when repository updates`() = runTest {
        val initial = RecipePage(list = emptyList(), totalResults = 0)
        val updated = RecipePage(list = listOf(null), totalResults = 1)
        val stateFlow = MutableStateFlow(initial)
        every { repository.recipePage } returns stateFlow

        val flow = useCase()
        assertEquals(initial, flow.first())

        stateFlow.value = updated
        assertEquals(updated, flow.first())
    }
}
