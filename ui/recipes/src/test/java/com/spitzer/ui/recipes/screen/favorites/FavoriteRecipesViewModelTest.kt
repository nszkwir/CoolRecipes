package com.spitzer.ui.recipes.screen.favorites

import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.usecase.favorites.GetFavoriteRecipesUseCase
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.URL

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteRecipesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase = mockk()
    private val favoritesFlow = MutableStateFlow<List<Recipe>>(emptyList())

    private val sampleRecipe1 = Recipe(
        id = 1L,
        title = "Pizza Margherita",
        image = URL("https://example.com/pizza.jpg"),
        summary = "Classic Italian pizza"
)

    private val sampleRecipe2 = Recipe(
        id = 2L,
        title = "Chicken Soup",
        image = URL("https://example.com/soup.jpg"),
        summary = "Warm chicken soup"
)

    private val sampleRecipe3 = Recipe(
        id = 3L,
        title = "Caesar Salad",
        image = null,
        summary = "Fresh Caesar salad"
)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getFavoriteRecipesUseCase() } returns favoritesFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): FavoriteRecipesViewModel {
        return FavoriteRecipesViewModel(getFavoriteRecipesUseCase)
    }

    @Test
    fun `initial state has loading true and empty list`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        assertTrue(viewModel.viewState.value.isLoading)
        assertTrue(viewModel.viewState.value.recipeList.isEmpty())
    }

    @Test
    fun `viewState updates when favorites flow emits`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        favoritesFlow.value = listOf(sampleRecipe1, sampleRecipe2)
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.isLoading)
        assertEquals(2, viewModel.viewState.value.recipeList.size)
        assertEquals("Pizza Margherita", viewModel.viewState.value.recipeList[0]?.title)
    }

    @Test
    fun `viewState updates with empty list when favorites are cleared`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        favoritesFlow.value = listOf(sampleRecipe1)
        advanceUntilIdle()
        assertEquals(1, viewModel.viewState.value.recipeList.size)

        favoritesFlow.value = emptyList()
        advanceUntilIdle()
        assertTrue(viewModel.viewState.value.recipeList.isEmpty())
    }

    @Test
    fun `onRecipeCardClicked emits RecipeDetail output`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        var outputResult: FavoriteRecipesViewModelOutput? = null
        viewModel.output = { outputResult = it }

        favoritesFlow.value = listOf(sampleRecipe1)
        advanceUntilIdle()

        // Trigger via view state callback
        viewModel.onRecipeCardClicked(sampleRecipe1)

        assertTrue(outputResult is FavoriteRecipesViewModelOutput.RecipeDetail)
        assertEquals(1L, (outputResult as FavoriteRecipesViewModelOutput.RecipeDetail).recipeId)
    }

    @Test
    fun `search filters favorites by title`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        favoritesFlow.value = listOf(sampleRecipe1, sampleRecipe2, sampleRecipe3)
        advanceUntilIdle()

        // Trigger search via view state callback
        viewModel.onQueryChange("Pizza")
        advanceUntilIdle()

        assertEquals("Pizza", viewModel.viewState.value.searchBarViewState.query)
        val searchResults = viewModel.viewState.value.searchBarViewState.recipesList
        assertEquals(1, searchResults.size)
        assertEquals("Pizza Margherita", searchResults[0].title)
    }

    @Test
    fun `empty search query shows all favorites in search results`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        favoritesFlow.value = listOf(sampleRecipe1, sampleRecipe2)
        advanceUntilIdle()

        // Filter first
        viewModel.onQueryChange("Pizza")
        advanceUntilIdle()
        assertEquals(1, viewModel.viewState.value.searchBarViewState.recipesList.size)

        // Clear filter
        viewModel.onQueryChange("")
        advanceUntilIdle()
        assertEquals(2, viewModel.viewState.value.searchBarViewState.recipesList.size)
    }

    @Test
    fun `search is case insensitive`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        favoritesFlow.value = listOf(sampleRecipe1, sampleRecipe2)
        advanceUntilIdle()

        viewModel.onQueryChange("pizza")
        advanceUntilIdle()

        val searchResults = viewModel.viewState.value.searchBarViewState.recipesList
        assertEquals(1, searchResults.size)
        assertEquals("Pizza Margherita", searchResults[0].title)
    }

    @Test
    fun `onOpenSearch sets isSearchActive to true`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        favoritesFlow.value = listOf(sampleRecipe1)
        advanceUntilIdle()

        viewModel.onOpenSearch()

        assertTrue(viewModel.viewState.value.searchBarViewState.isSearchActive)
    }

    @Test
    fun `onCloseSearch resets search state`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        favoritesFlow.value = listOf(sampleRecipe1)
        advanceUntilIdle()

        // Open search and enter query
        viewModel.onOpenSearch()
        viewModel.onQueryChange("test")
        advanceUntilIdle()

        // Close search
        viewModel.onCloseSearch()

        assertFalse(viewModel.viewState.value.searchBarViewState.isSearchActive)
        assertEquals("", viewModel.viewState.value.searchBarViewState.query)
        assertTrue(viewModel.viewState.value.searchBarViewState.recipesList.isEmpty())
    }

    @Test
    fun `refreshFavorites sets isLoading to false`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        favoritesFlow.value = listOf(sampleRecipe1)
        advanceUntilIdle()

        viewModel.refreshFavorites()

        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `search with no matches returns empty list`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        favoritesFlow.value = listOf(sampleRecipe1, sampleRecipe2)
        advanceUntilIdle()

        viewModel.onQueryChange("xyz")
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.searchBarViewState.recipesList.isEmpty())
    }
}
