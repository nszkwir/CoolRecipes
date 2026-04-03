package com.spitzer.ui.recipes.screen.list

import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.model.recipe.RecipePage
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.domain.usecase.recipe.FetchNextRecipePageWhenNeededUseCase
import com.spitzer.domain.usecase.recipe.GetRecipeListUseCase
import com.spitzer.domain.usecase.recipe.RefreshRecipeListUseCase
import com.spitzer.domain.usecase.recipe.SearchRecipePageUseCase
import com.spitzer.domain.usecase.recipe.result.RecipePaginationResult
import com.spitzer.domain.usecase.recipe.result.SearchRecipeResult
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
class RecipeListScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getRecipeListUseCase: GetRecipeListUseCase = mockk()
    private val refreshRecipeListUseCase: RefreshRecipeListUseCase = mockk()
    private val fetchNextRecipePageWhenNeededUseCase: FetchNextRecipePageWhenNeededUseCase = mockk()
    private val searchRecipeListUseCase: SearchRecipePageUseCase = mockk()

    private val recipePageFlow = MutableStateFlow(RecipePage(list = emptyList(), totalResults = 0))

    private val sampleRecipe = Recipe(
        id = 1L,
        title = "Test Recipe",
        image = URL("https://example.com/recipe.jpg"),
        summary = "Test summary"
)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getRecipeListUseCase() } returns recipePageFlow
        coEvery {
            refreshRecipeListUseCase(any(), any())
        } returns RecipePaginationResult.Success
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): RecipeListScreenViewModel {
        return RecipeListScreenViewModel(
            getRecipeListUseCase = getRecipeListUseCase,
            refreshRecipeListUseCase = refreshRecipeListUseCase,
            fetchNextRecipePageWhenNeededUseCase = fetchNextRecipePageWhenNeededUseCase,
            searchRecipeListUseCase = searchRecipeListUseCase
        )
    }

    @Test
    fun `initial state has isLoading true and empty list`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        assertTrue(viewModel.viewState.value.isLoading)
        assertTrue(viewModel.viewState.value.recipeList.isEmpty())
    }

    @Test
    fun `init calls refreshRecipeList and collects recipe page`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        coVerify {
            refreshRecipeListUseCase(
                sortCriteria = RecipeSortCriteria.RELEVANCE,
                sortOrder = RecipeSortOrder.DESCENDING
            )
        }
    }

    @Test
    fun `recipe page updates when flow emits`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        recipePageFlow.value = RecipePage(list = listOf(sampleRecipe), totalResults = 1)
        advanceUntilIdle()

        assertEquals(1, viewModel.viewState.value.recipeList.size)
        assertEquals("Test Recipe", viewModel.viewState.value.recipeList[0]?.title)
    }

    @Test
    fun `refreshRecipeList success hides loading and clears error`() = runTest(testDispatcher) {
        coEvery {
            refreshRecipeListUseCase(any(), any())
        } returns RecipePaginationResult.Success
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.isLoading)
        assertNull(viewModel.viewState.value.message)
    }

    @Test
    fun `refreshRecipeList NoInternet shows error when list is empty`() = runTest(testDispatcher) {
        coEvery {
            refreshRecipeListUseCase(any(), any())
        } returns RecipePaginationResult.NoInternet
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertNotNull(viewModel.viewState.value.message)
        assertEquals(
            RecipeListScreenViewState.Message.Type.NO_INTERNET,
            viewModel.viewState.value.message?.type
        )
    }

    @Test
    fun `refreshRecipeList Unknown shows generic error when list is empty`() = runTest(testDispatcher) {
        coEvery {
            refreshRecipeListUseCase(any(), any())
        } returns RecipePaginationResult.Unknown
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertNotNull(viewModel.viewState.value.message)
        assertEquals(
            RecipeListScreenViewState.Message.Type.GENERIC,
            viewModel.viewState.value.message?.type
        )
    }

    @Test
    fun `refreshRecipeList NoInternet does not show error when list has items`() = runTest(testDispatcher) {
        // Create viewModel with default (Success) refresh so init completes
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Now populate the list via the flow
        recipePageFlow.value = RecipePage(list = listOf(sampleRecipe), totalResults = 1)
        advanceUntilIdle()

        // Now mock NoInternet and trigger a refresh
        coEvery {
            refreshRecipeListUseCase(any(), any())
        } returns RecipePaginationResult.NoInternet
        viewModel.refreshRecipeList()
        advanceUntilIdle()

        assertNull(viewModel.viewState.value.message)
    }

    @Test
    fun `onRecipeCardClicked emits RecipeDetail output`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        var outputResult: RecipeListScreenViewModelOutput? = null
        viewModel.output = { outputResult = it }
        advanceUntilIdle()

        viewModel.onRecipeCardClicked(sampleRecipe)

        assertTrue(outputResult is RecipeListScreenViewModelOutput.RecipeDetail)
        assertEquals(1L, (outputResult as RecipeListScreenViewModelOutput.RecipeDetail).recipeId)
    }

    @Test
    fun `fetchNextPage calls use case with correct params`() = runTest(testDispatcher) {
        coEvery {
            fetchNextRecipePageWhenNeededUseCase(any(), any(), any())
        } returns RecipePaginationResult.Success
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.getRecipeList(15)
        advanceUntilIdle()

        coVerify {
            fetchNextRecipePageWhenNeededUseCase(
                elementIndex = 15,
                sortCriteria = RecipeSortCriteria.RELEVANCE,
                sortOrder = RecipeSortOrder.DESCENDING
            )
        }
    }

    @Test
    fun `fetchNextPage NoInternet shows error`() = runTest(testDispatcher) {
        coEvery {
            fetchNextRecipePageWhenNeededUseCase(any(), any(), any())
        } returns RecipePaginationResult.NoInternet
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.getRecipeList(15)
        advanceUntilIdle()

        assertNotNull(viewModel.viewState.value.message)
        assertEquals(
            RecipeListScreenViewState.Message.Type.NO_INTERNET,
            viewModel.viewState.value.message?.type
        )
    }

    @Test
    fun `search with query longer than 2 chars calls search use case`() = runTest(testDispatcher) {
        val searchResults = listOf(sampleRecipe)
        coEvery {
            searchRecipeListUseCase(any(), any(), any(), any())
        } returns SearchRecipeResult.Success(searchResults)
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Open search first
        viewModel.onOpenSearch()
        // Type query > 2 chars
        viewModel.onQueryChange("pas")
        advanceUntilIdle()

        coVerify {
            searchRecipeListUseCase(
                query = "pas",
                searchCriteria = RecipeSearchCriteria.NAME,
                sortCriteria = RecipeSortCriteria.RELEVANCE,
                sortOrder = RecipeSortOrder.DESCENDING
            )
        }
    }

    @Test
    fun `search success populates search results`() = runTest(testDispatcher) {
        val searchResults = listOf(sampleRecipe)
        coEvery {
            searchRecipeListUseCase(any(), any(), any(), any())
        } returns SearchRecipeResult.Success(searchResults)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("test")
        advanceUntilIdle()

        assertEquals(1, viewModel.viewState.value.searchBarViewState.recipesList.size)
        assertEquals("Test Recipe", viewModel.viewState.value.searchBarViewState.recipesList[0].title)
    }

    @Test
    fun `search with 2 or fewer chars does not call search use case`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("ab")
        advanceUntilIdle()

        coVerify(exactly = 0) {
            searchRecipeListUseCase(any(), any(), any(), any())
        }
    }

    @Test
    fun `search NoInternet shows no internet error`() = runTest(testDispatcher) {
        coEvery {
            searchRecipeListUseCase(any(), any(), any(), any())
        } returns SearchRecipeResult.NoInternet
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("pasta")
        advanceUntilIdle()

        assertNotNull(viewModel.viewState.value.message)
        assertEquals(
            RecipeListScreenViewState.Message.Type.NO_INTERNET,
            viewModel.viewState.value.message?.type
        )
    }

    @Test
    fun `search Unknown shows generic error`() = runTest(testDispatcher) {
        coEvery {
            searchRecipeListUseCase(any(), any(), any(), any())
        } returns SearchRecipeResult.Unknown
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("cake")
        advanceUntilIdle()

        assertNotNull(viewModel.viewState.value.message)
        assertEquals(
            RecipeListScreenViewState.Message.Type.GENERIC,
            viewModel.viewState.value.message?.type
        )
    }

    @Test
    fun `onOpenSearch sets isSearchActive true`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onOpenSearch()

        assertTrue(viewModel.viewState.value.searchBarViewState.isSearchActive)
    }

    @Test
    fun `onCloseSearch resets search state`() = runTest(testDispatcher) {
        coEvery {
            searchRecipeListUseCase(any(), any(), any(), any())
        } returns SearchRecipeResult.Success(listOf(sampleRecipe))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onOpenSearch()
        viewModel.onQueryChange("test")
        advanceUntilIdle()
        viewModel.onCloseSearch()

        assertFalse(viewModel.viewState.value.searchBarViewState.isSearchActive)
        assertEquals("", viewModel.viewState.value.searchBarViewState.query)
        assertTrue(viewModel.viewState.value.searchBarViewState.recipesList.isEmpty())
    }

    @Test
    fun `empty query clears search results`() = runTest(testDispatcher) {
        coEvery {
            searchRecipeListUseCase(any(), any(), any(), any())
        } returns SearchRecipeResult.Success(listOf(sampleRecipe))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("test")
        advanceUntilIdle()
        assertEquals(1, viewModel.viewState.value.searchBarViewState.recipesList.size)

        viewModel.onQueryChange("")
        advanceUntilIdle()
        assertTrue(viewModel.viewState.value.searchBarViewState.recipesList.isEmpty())
    }

    @Test
    fun `onFunnelTap shows bottom sheet`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onFunnelTap()

        assertFalse(viewModel.viewState.value.bottomSheetViewState.shouldHide)
    }

    @Test
    fun `bottom sheet default filter values`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val bottomSheet = viewModel.viewState.value.bottomSheetViewState
        assertEquals(RecipeSearchCriteria.NAME, bottomSheet.selectedSearchCriteria)
        assertEquals(RecipeSortCriteria.RELEVANCE, bottomSheet.selectedSortCriteria)
        assertEquals(RecipeSortOrder.DESCENDING, bottomSheet.selectedSortOrder)
        assertTrue(bottomSheet.shouldHide)
    }

    @Test
    fun `onSearchCriteriaSelected updates bottom sheet and search bar`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSearchCriteriaSelected(RecipeSearchCriteria.INGREDIENTS)

        assertEquals(
            RecipeSearchCriteria.INGREDIENTS,
            viewModel.viewState.value.bottomSheetViewState.selectedSearchCriteria
        )
        assertEquals(
            RecipeSearchCriteria.INGREDIENTS,
            viewModel.viewState.value.searchBarViewState.searchCriteria
        )
    }

    @Test
    fun `onSortCriteriaSelected updates bottom sheet`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSortCriteriaSelected(RecipeSortCriteria.POPULARITY)

        assertEquals(
            RecipeSortCriteria.POPULARITY,
            viewModel.viewState.value.bottomSheetViewState.selectedSortCriteria
        )
    }

    @Test
    fun `onSortOrderSelected updates bottom sheet`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSortOrderSelected(RecipeSortOrder.ASCENDING)

        assertEquals(
            RecipeSortOrder.ASCENDING,
            viewModel.viewState.value.bottomSheetViewState.selectedSortOrder
        )
    }

    @Test
    fun `clearSearchFilters resets to defaults and refreshes`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Change criteria first
        viewModel.onSortCriteriaSelected(RecipeSortCriteria.POPULARITY)
        viewModel.clearSearchFilters()
        advanceUntilIdle()

        val bottomSheet = viewModel.viewState.value.bottomSheetViewState
        assertEquals(RecipeSearchCriteria.NAME, bottomSheet.selectedSearchCriteria)
        assertEquals(RecipeSortCriteria.RELEVANCE, bottomSheet.selectedSortCriteria)
        assertEquals(RecipeSortOrder.DESCENDING, bottomSheet.selectedSortOrder)
        assertTrue(bottomSheet.shouldHide)
    }

    @Test
    fun `confirmSearchFilters triggers refresh when criteria changed`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Change sort criteria
        viewModel.onSortCriteriaSelected(RecipeSortCriteria.CALORIES)
        viewModel.confirmSearchFilters()
        advanceUntilIdle()

        // Verify refreshRecipeList was called with the new criteria
        coVerify {
            refreshRecipeListUseCase(
                sortCriteria = RecipeSortCriteria.CALORIES,
                sortOrder = RecipeSortOrder.DESCENDING
            )
        }
    }

    @Test
    fun `showNoInternetConnectionError sets message type`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.showNoInternetConnectionError(
            
        )

        assertEquals(
            RecipeListScreenViewState.Message.Type.NO_INTERNET,
            viewModel.viewState.value.message?.type
        )
    }

    @Test
    fun `showGenericError sets message type`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.showGenericError(
            
        )

        assertEquals(
            RecipeListScreenViewState.Message.Type.GENERIC,
            viewModel.viewState.value.message?.type
        )
    }

    @Test
    fun `search bar funnel is not on by default`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.searchBarViewState.isFunnelOn)
    }

    @Test
    fun `funnel turns on when criteria changes from defaults`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Change sort criteria and confirm
        viewModel.onSortCriteriaSelected(RecipeSortCriteria.POPULARITY)
        viewModel.confirmSearchFilters()
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.searchBarViewState.isFunnelOn)
    }

    @Test
    fun `search bar has funnel enabled`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.searchBarViewState.isFunnelEnabled)
    }
}
