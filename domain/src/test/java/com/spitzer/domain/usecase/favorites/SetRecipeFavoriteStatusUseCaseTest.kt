package com.spitzer.domain.usecase.favorites

import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.favorites.result.SetRecipeFavoriteStatusResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SetRecipeFavoriteStatusUseCaseTest {

    private val repository: RecipeRepository = mockk()
    private val useCase = SetRecipeFavoriteStatusUseCase(repository)

    @Test
    fun `invoke delegates to repository setRecipeFavorite when adding favorite`() = runTest {
        coEvery {
            repository.setRecipeFavorite(any(), any(), any(), any(), any())
        } returns SetRecipeFavoriteStatusResult.Success

        val result = useCase(
            id = 1L,
            isFavorite = true,
            title = "Pizza",
            image = "https://example.com/pizza.jpg",
            summary = "Delicious pizza"
        )

        assertEquals(SetRecipeFavoriteStatusResult.Success, result)
        coVerify {
            repository.setRecipeFavorite(
                id = 1L,
                isFavorite = true,
                title = "Pizza",
                image = "https://example.com/pizza.jpg",
                summary = "Delicious pizza"
            )
        }
    }

    @Test
    fun `invoke delegates to repository setRecipeFavorite when removing favorite`() = runTest {
        coEvery {
            repository.setRecipeFavorite(any(), any(), any(), any(), any())
        } returns SetRecipeFavoriteStatusResult.Success

        val result = useCase(
            id = 2L,
            isFavorite = false,
            title = "Salad",
            image = null,
            summary = "Healthy salad"
        )

        assertEquals(SetRecipeFavoriteStatusResult.Success, result)
        coVerify {
            repository.setRecipeFavorite(
                id = 2L,
                isFavorite = false,
                title = "Salad",
                image = null,
                summary = "Healthy salad"
            )
        }
    }

    @Test
    fun `invoke returns Error when repository returns Error`() = runTest {
        coEvery {
            repository.setRecipeFavorite(any(), any(), any(), any(), any())
        } returns SetRecipeFavoriteStatusResult.Error

        val result = useCase(
            id = 3L,
            isFavorite = true,
            title = "Soup",
            image = "https://example.com/soup.jpg",
            summary = "Warm soup"
        )

        assertEquals(SetRecipeFavoriteStatusResult.Error, result)
    }

    @Test
    fun `invoke passes null image correctly`() = runTest {
        coEvery {
            repository.setRecipeFavorite(any(), any(), any(), any(), any())
        } returns SetRecipeFavoriteStatusResult.Success

        useCase(
            id = 4L,
            isFavorite = true,
            title = "Bread",
            image = null,
            summary = "Fresh bread"
        )

        coVerify {
            repository.setRecipeFavorite(
                id = 4L,
                isFavorite = true,
                title = "Bread",
                image = null,
                summary = "Fresh bread"
            )
        }
    }
}
