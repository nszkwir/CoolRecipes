package com.spitzer.domain.usecase.favorites

import com.spitzer.domain.repository.RecipeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRecipeFavoriteStatusUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = GetRecipeFavoriteStatusUseCase(repository)

    @Test
    fun `invoke returns true when recipe id is in favorites`() = runTest {
        val favoriteIds = MutableStateFlow(setOf(1L, 2L, 3L))
        every { repository.favoriteRecipeIds } returns favoriteIds

        val result = useCase(2L).first()

        assertTrue(result)
        verify { repository.favoriteRecipeIds }
    }

    @Test
    fun `invoke returns false when recipe id is not in favorites`() = runTest {
        val favoriteIds = MutableStateFlow(setOf(1L, 2L, 3L))
        every { repository.favoriteRecipeIds } returns favoriteIds

        val result = useCase(99L).first()

        assertFalse(result)
    }

    @Test
    fun `invoke returns false when favorites set is empty`() = runTest {
        val favoriteIds = MutableStateFlow(emptySet<Long>())
        every { repository.favoriteRecipeIds } returns favoriteIds

        val result = useCase(1L).first()

        assertFalse(result)
    }

    @Test
    fun `invoke emits updated value when favorites change`() = runTest {
        val favoriteIds = MutableStateFlow(setOf(1L))
        every { repository.favoriteRecipeIds } returns favoriteIds

        val flow = useCase(2L)
        assertFalse(flow.first())

        favoriteIds.value = setOf(1L, 2L)
        assertTrue(flow.first())
    }
}
