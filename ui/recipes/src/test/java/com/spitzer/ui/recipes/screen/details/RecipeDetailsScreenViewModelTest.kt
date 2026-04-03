package com.spitzer.ui.recipes.screen.details

import com.spitzer.domain.model.recipe.RecipeDetails
import com.spitzer.domain.usecase.favorites.GetRecipeFavoriteStatusUseCase
import com.spitzer.domain.usecase.favorites.SetRecipeFavoriteStatusUseCase
import com.spitzer.domain.usecase.favorites.result.SetRecipeFavoriteStatusResult
import com.spitzer.domain.usecase.recipedetails.GetRecipeDetailsByIdUseCase
import com.spitzer.domain.usecase.recipedetails.RefreshRecipeDetailsByIdUseCase
import com.spitzer.domain.usecase.recipedetails.result.RecipeDetailsResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.URL

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailsScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getRecipeByIdUseCase: GetRecipeDetailsByIdUseCase = mockk()
    private val refreshRecipeDetailsByIdUseCase: RefreshRecipeDetailsByIdUseCase = mockk()
    private val getRecipeFavoriteStatusUseCase: GetRecipeFavoriteStatusUseCase = mockk()
    private val setRecipeFavoriteStatusUseCase: SetRecipeFavoriteStatusUseCase = mockk()
    private val favoriteStatusFlow = MutableStateFlow(false)
    private val recipeId = 42L

    private val sampleRecipeDetails = RecipeDetails(
        id = recipeId,
        title = "Test Recipe",
        readyInMinutes = 30,
        servings = 4,
        summary = "A test recipe",
        instructions = "Cook it well",
        vegetarian = false,
        vegan = false,
        glutenFree = true,
        dairyFree = false,
        image = URL("https://example.com/recipe.jpg"),
        healthScore = 75.0,
        diets = listOf("gluten free"),
        spoonacularScore = 85.0,
        spoonacularSourceUrl = URL("https://spoonacular.com/recipe-42"),
        ingredients = listOf("flour", "sugar", "eggs")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getRecipeFavoriteStatusUseCase(recipeId) } returns favoriteStatusFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        getResult: RecipeDetailsResult = RecipeDetailsResult.Success(sampleRecipeDetails)
    ): RecipeDetailsScreenViewModel {
        coEvery { getRecipeByIdUseCase(recipeId) } returns getResult
        return RecipeDetailsScreenViewModel(
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            refreshRecipeDetailsByIdUseCase = refreshRecipeDetailsByIdUseCase,
            getRecipeFavoriteStatusUseCase = getRecipeFavoriteStatusUseCase,
            setRecipeFavoriteStatusUseCase = setRecipeFavoriteStatusUseCase,
            input = RecipeDetailsScreenViewModelInput(recipeId = recipeId)
        )
    }

    @Test
    fun `initial state has loading true`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        assertTrue(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `loadRecipeDetails on success populates view state`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertFalse(state.isLoading)
        assertNotNull(state.recipeDetails)
        assertEquals("Test Recipe", state.recipeDetails?.title)
        assertEquals("A test recipe", state.recipeDetails?.summary)
        assertEquals("Cook it well", state.recipeDetails?.instructions)
        assertEquals(30, state.recipeDetails?.readyInMinutes)
        assertEquals(4, state.recipeDetails?.servings)
        assertNull(state.message)
    }

    @Test
    fun `loadRecipeDetails on NoInternet shows no internet message`() = runTest(testDispatcher) {
        val viewModel = createViewModel(getResult = RecipeDetailsResult.NoInternet)
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertFalse(state.isLoading)
        assertNotNull(state.message)
        assertEquals(RecipeDetailsScreenViewState.Message.Type.NO_INTERNET, state.message?.type)
    }

    @Test
    fun `loadRecipeDetails on Unknown shows generic error message`() = runTest(testDispatcher) {
        val viewModel = createViewModel(getResult = RecipeDetailsResult.Unknown)
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertFalse(state.isLoading)
        assertNotNull(state.message)
        assertEquals(RecipeDetailsScreenViewState.Message.Type.GENERIC, state.message?.type)
    }

    @Test
    fun `onRefresh calls refreshRecipeDetailsByIdUseCase`() = runTest(testDispatcher) {
        coEvery { refreshRecipeDetailsByIdUseCase(recipeId) } returns RecipeDetailsResult.Success(sampleRecipeDetails)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        coVerify { refreshRecipeDetailsByIdUseCase(recipeId) }
    }

    @Test
    fun `onRefresh with success updates recipe details`() = runTest(testDispatcher) {
        val updatedDetails = sampleRecipeDetails.copy(title = "Updated Recipe")
        coEvery { refreshRecipeDetailsByIdUseCase(recipeId) } returns RecipeDetailsResult.Success(updatedDetails)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        assertEquals("Updated Recipe", viewModel.viewState.value.recipeDetails?.title)
        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `onRefresh with NoInternet shows no internet error`() = runTest(testDispatcher) {
        coEvery { refreshRecipeDetailsByIdUseCase(recipeId) } returns RecipeDetailsResult.NoInternet
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        assertNotNull(viewModel.viewState.value.message)
        assertEquals(RecipeDetailsScreenViewState.Message.Type.NO_INTERNET, viewModel.viewState.value.message?.type)
    }

    @Test
    fun `onBackButtonPressed calls output with ScreenNavigateBack`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        var outputResult: RecipeDetailsScreenViewModelOutput? = null
        viewModel.output = { outputResult = it }
        advanceUntilIdle()

        viewModel.onBackButtonPressed()

        assertTrue(outputResult is RecipeDetailsScreenViewModelOutput.ScreenNavigateBack)
    }

    @Test
    fun `onFavoriteTapped toggles favorite and calls use case`() = runTest(testDispatcher) {
        coEvery {
            setRecipeFavoriteStatusUseCase(any(), any(), any(), any(), any())
        } returns SetRecipeFavoriteStatusResult.Success
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onFavoriteTapped()
        advanceUntilIdle()

        coVerify {
            setRecipeFavoriteStatusUseCase(
                id = recipeId,
                isFavorite = true,
                title = "Test Recipe",
                image = "https://example.com/recipe.jpg",
                summary = "A test recipe"
            )
        }
    }

    @Test
    fun `onFavoriteTapped with error shows generic error`() = runTest(testDispatcher) {
        coEvery {
            setRecipeFavoriteStatusUseCase(any(), any(), any(), any(), any())
        } returns SetRecipeFavoriteStatusResult.Error
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onFavoriteTapped()
        advanceUntilIdle()

        assertNotNull(viewModel.viewState.value.message)
        assertEquals(RecipeDetailsScreenViewState.Message.Type.GENERIC, viewModel.viewState.value.message?.type)
    }

    @Test
    fun `onFavoriteTapped does nothing when no recipe details loaded`() = runTest(testDispatcher) {
        val viewModel = createViewModel(getResult = RecipeDetailsResult.NoInternet)
        advanceUntilIdle()

        viewModel.onFavoriteTapped()
        advanceUntilIdle()

        coVerify(exactly = 0) {
            setRecipeFavoriteStatusUseCase(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `favorite status flow updates isFavorite in view state`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        favoriteStatusFlow.value = true
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.recipeDetails?.isFavorite == true)

        favoriteStatusFlow.value = false
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.recipeDetails?.isFavorite == true)
    }

    @Test
    fun `onMessagePrimaryButtonClicked reloads recipe details`() = runTest(testDispatcher) {
        val viewModel = createViewModel(getResult = RecipeDetailsResult.NoInternet)
        advanceUntilIdle()

        // Now make the next call succeed
        coEvery { getRecipeByIdUseCase(recipeId) } returns RecipeDetailsResult.Success(sampleRecipeDetails)
        viewModel.onMessagePrimaryButtonClicked()
        advanceUntilIdle()

        assertNotNull(viewModel.viewState.value.recipeDetails)
        assertEquals("Test Recipe", viewModel.viewState.value.recipeDetails?.title)
    }

    @Test
    fun `onMessageSecondaryButtonClicked calls output ScreenNavigateBack`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        var outputResult: RecipeDetailsScreenViewModelOutput? = null
        viewModel.output = { outputResult = it }
        advanceUntilIdle()

        viewModel.onMessageSecondaryButtonClicked()

        assertTrue(outputResult is RecipeDetailsScreenViewModelOutput.ScreenNavigateBack)
    }

    @Test
    fun `showNoInternetConnectionError sets correct message type`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.showNoInternetConnectionError()

        assertEquals(RecipeDetailsScreenViewState.Message.Type.NO_INTERNET, viewModel.viewState.value.message?.type)
    }

    @Test
    fun `showGenericError sets correct message type`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.showGenericError()

        assertEquals(RecipeDetailsScreenViewState.Message.Type.GENERIC, viewModel.viewState.value.message?.type)
    }
}
