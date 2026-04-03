package com.spitzer.data.repository.recipe

import com.spitzer.data.local.recipe.dao.FavoriteRecipeDao
import com.spitzer.data.local.recipe.dao.RecipeDetailsDao
import com.spitzer.data.local.recipe.dao.RecipesDao
import com.spitzer.data.local.recipe.entities.FavoriteRecipeEntity
import com.spitzer.data.local.recipe.entities.RecipeDetailsEntity
import com.spitzer.data.local.recipe.entities.RecipeEntity
import com.spitzer.data.local.recipe.sharedpreferences.RecipeSharedPreferences
import com.spitzer.data.remote.common.ApiError
import com.spitzer.data.remote.common.ApiResult
import com.spitzer.data.remote.recipe.api.RecipeService
import com.spitzer.data.remote.recipe.api.dto.RecipeDetailsResponse
import com.spitzer.data.remote.recipe.api.dto.RecipePageResponse
import com.spitzer.data.remote.recipe.api.dto.RecipeResponse
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.domain.usecase.favorites.result.SetRecipeFavoriteStatusResult
import com.spitzer.domain.usecase.recipe.result.RecipePaginationResult
import com.spitzer.domain.usecase.recipe.result.SearchRecipeResult
import com.spitzer.domain.usecase.recipedetails.result.RecipeDetailsResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecipeRepositoryImplTest {

    private val recipeService: RecipeService = mockk()
    private val recipesDao: RecipesDao = mockk(relaxed = true)
    private val recipeDetailsDao: RecipeDetailsDao = mockk(relaxed = true)
    private val favoriteRecipeDao: FavoriteRecipeDao = mockk(relaxed = true)
    private val recipePreferences: RecipeSharedPreferences = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: RecipeRepositoryImpl

    @Before
    fun setup() {
        every { favoriteRecipeDao.getAll() } returns flowOf(emptyList())
        repository = RecipeRepositoryImpl(
            recipeService = recipeService,
            recipesDao = recipesDao,
            recipeDetailsDao = recipeDetailsDao,
            favoriteRecipeDao = favoriteRecipeDao,
            recipePreferences = recipePreferences,
            ioDispatcher = testDispatcher
        )
    }

    // region initialize

    @Test
    fun `initialize loads recipes from database and sets recipePage`() = runTest(testDispatcher) {
        val entities = listOf(
            RecipeEntity(index = 0, id = 1L, title = "Recipe 1", image = "https://example.com/1.jpg", summary = "Summary 1"),
            RecipeEntity(index = 1, id = 2L, title = "Recipe 2", image = "https://example.com/2.jpg", summary = "Summary 2")
        )
        every { recipesDao.get() } returns entities
        every { recipePreferences.getRecipeListTotalResults() } returns 5

        repository.initialize()

        val page = repository.recipePage.value
        assertEquals(5, page.totalResults)
        assertEquals(5, page.list.size)
        assertNotNull(page.list[0])
        assertNotNull(page.list[1])
        assertNull(page.list[2])
        assertEquals("Recipe 1", page.list[0]?.title)
        assertEquals("Recipe 2", page.list[1]?.title)
    }

    @Test
    fun `initialize with empty database sets empty recipe page`() = runTest(testDispatcher) {
        every { recipesDao.get() } returns emptyList()
        every { recipePreferences.getRecipeListTotalResults() } returns 0

        repository.initialize()

        val page = repository.recipePage.value
        assertEquals(0, page.totalResults)
        assertTrue(page.list.isEmpty())
    }

    @Test
    fun `initialize sets empty recipes on exception`() = runTest(testDispatcher) {
        every { recipesDao.get() } throws RuntimeException("Database error")

        repository.initialize()

        val page = repository.recipePage.value
        assertEquals(0, page.totalResults)
        assertTrue(page.list.isEmpty())
    }

    // endregion

    // region setRecipeFavorite

    @Test
    fun `setRecipeFavorite adds favorite when isFavorite is true`() = runTest {
        coEvery { favoriteRecipeDao.upsert(any()) } returns Unit

        val result = repository.setRecipeFavorite(
            id = 1L,
            isFavorite = true,
            title = "Pizza",
            image = "https://example.com/pizza.jpg",
            summary = "Delicious pizza"
        )

        assertEquals(SetRecipeFavoriteStatusResult.Success, result)
        coVerify {
            favoriteRecipeDao.upsert(
                FavoriteRecipeEntity(
                    id = 1L,
                    title = "Pizza",
                    image = "https://example.com/pizza.jpg",
                    summary = "Delicious pizza"
                )
            )
        }
    }

    @Test
    fun `setRecipeFavorite removes favorite when isFavorite is false`() = runTest {
        coEvery { favoriteRecipeDao.deleteById(any()) } returns Unit

        val result = repository.setRecipeFavorite(
            id = 2L,
            isFavorite = false,
            title = "Salad",
            image = null,
            summary = "Healthy"
        )

        assertEquals(SetRecipeFavoriteStatusResult.Success, result)
        coVerify { favoriteRecipeDao.deleteById(2L) }
    }

    @Test
    fun `setRecipeFavorite with null image upserts correctly`() = runTest {
        coEvery { favoriteRecipeDao.upsert(any()) } returns Unit

        val result = repository.setRecipeFavorite(
            id = 3L,
            isFavorite = true,
            title = "Soup",
            image = null,
            summary = "Warm soup"
        )

        assertEquals(SetRecipeFavoriteStatusResult.Success, result)
        coVerify {
            favoriteRecipeDao.upsert(
                FavoriteRecipeEntity(
                    id = 3L,
                    title = "Soup",
                    image = null,
                    summary = "Warm soup"
                )
            )
        }
    }

    // endregion

    // region refreshRecipeList

    @Test
    fun `refreshRecipeList returns Success on successful API response`() = runTest {
        val response = RecipePageResponse(
            results = listOf(
                RecipeResponse(id = 1L, title = "Pasta", image = "https://example.com/1.jpg", summary = "Summary 1")
            ),
            totalResults = 1
        )
        coEvery {
            recipeService.fetchRecipes(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Success(response)

        val result = repository.refreshRecipeList(
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(RecipePaginationResult.Success, result)
        val page = repository.recipePage.value
        assertEquals(1, page.totalResults)
        assertEquals("Pasta", page.list[0]?.title)
    }

    @Test
    fun `refreshRecipeList returns NoInternet on NoInternet API error`() = runTest {
        coEvery {
            recipeService.fetchRecipes(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Failure(ApiError.NoInternet)

        val result = repository.refreshRecipeList(
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(RecipePaginationResult.NoInternet, result)
    }

    @Test
    fun `refreshRecipeList returns Unknown on generic API error`() = runTest {
        coEvery {
            recipeService.fetchRecipes(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Failure(ApiError.Unknown())

        val result = repository.refreshRecipeList(
            sortCriteria = RecipeSortCriteria.POPULARITY,
            sortOrder = RecipeSortOrder.ASCENDING
        )

        assertEquals(RecipePaginationResult.Unknown, result)
    }

    @Test
    fun `refreshRecipeList returns Empty when API returns empty results`() = runTest {
        val response = RecipePageResponse(
            results = emptyList(),
            totalResults = 0
        )
        coEvery {
            recipeService.fetchRecipes(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Success(response)

        val result = repository.refreshRecipeList(
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(RecipePaginationResult.Empty, result)
    }

    @Test
    fun `refreshRecipeList persists items to database on success`() = runTest {
        val response = RecipePageResponse(
            results = listOf(
                RecipeResponse(id = 1L, title = "Pasta", image = "https://example.com/1.jpg", summary = "Summary 1")
            ),
            totalResults = 1
        )
        coEvery {
            recipeService.fetchRecipes(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Success(response)

        repository.refreshRecipeList(
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        coVerify { recipesDao.upsert(any()) }
    }

    // endregion

    // region searchRecipeList

    @Test
    fun `searchRecipeList by NAME returns Success`() = runTest {
        val response = RecipePageResponse(
            results = listOf(
                RecipeResponse(id = 1L, title = "Pasta", image = "https://example.com/1.jpg", summary = "Summary")
            ),
            totalResults = 1
        )
        coEvery {
            recipeService.fetchRecipes(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Success(response)

        val result = repository.searchRecipeList(
            query = "pasta",
            searchCriteria = RecipeSearchCriteria.NAME,
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertTrue(result is SearchRecipeResult.Success)
        assertEquals(1, (result as SearchRecipeResult.Success).recipeList.size)

        coVerify {
            recipeService.fetchRecipes(
                offset = 0,
                limit = 20,
                query = "pasta",
                includeIngredients = null,
                sortCriteria = any(),
                sortOrder = any()
            )
        }
    }

    @Test
    fun `searchRecipeList by INGREDIENTS passes comma-separated ingredients`() = runTest {
        val response = RecipePageResponse(
            results = emptyList(),
            totalResults = 0
        )
        coEvery {
            recipeService.fetchRecipes(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Success(response)

        repository.searchRecipeList(
            query = "tomato basil",
            searchCriteria = RecipeSearchCriteria.INGREDIENTS,
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        coVerify {
            recipeService.fetchRecipes(
                offset = 0,
                limit = 20,
                query = null,
                includeIngredients = "tomato,basil",
                sortCriteria = any(),
                sortOrder = any()
            )
        }
    }

    @Test
    fun `searchRecipeList returns NoInternet on API NoInternet error`() = runTest {
        coEvery {
            recipeService.fetchRecipes(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Failure(ApiError.NoInternet)

        val result = repository.searchRecipeList(
            query = "pasta",
            searchCriteria = RecipeSearchCriteria.NAME,
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(SearchRecipeResult.NoInternet, result)
    }

    @Test
    fun `searchRecipeList returns Unknown on generic API error`() = runTest {
        coEvery {
            recipeService.fetchRecipes(any(), any(), any(), any(), any(), any())
        } returns ApiResult.Failure(ApiError.Unknown())

        val result = repository.searchRecipeList(
            query = "pasta",
            searchCriteria = RecipeSearchCriteria.NAME,
            sortCriteria = RecipeSortCriteria.RELEVANCE,
            sortOrder = RecipeSortOrder.DESCENDING
        )

        assertEquals(SearchRecipeResult.Unknown, result)
    }

    // endregion

    // region getRecipeDetailsById

    @Test
    fun `getRecipeDetailsById returns from database when found`() = runTest {
        val entity = RecipeDetailsEntity(
            id = 1L,
            title = "Test Recipe",
            readyInMinutes = 30,
            servings = 4,
            summary = "A test",
            instructions = "Cook it",
            vegetarian = false,
            vegan = false,
            glutenFree = true,
            dairyFree = false,
            image = "https://example.com/recipe.jpg",
            healthScore = 75.0,
            diets = listOf("gluten free"),
            spoonacularScore = 85.0,
            spoonacularSourceUrl = "https://spoonacular.com/1",
            ingredients = listOf("flour", "sugar")
        )
        coEvery { recipeDetailsDao.getRecipeById(1L) } returns entity

        val result = repository.getRecipeDetailsById(1L)

        assertTrue(result is RecipeDetailsResult.Success)
        assertEquals("Test Recipe", (result as RecipeDetailsResult.Success).recipeDetails.title)
    }

    @Test
    fun `getRecipeDetailsById fetches from API when not in database`() = runTest {
        coEvery { recipeDetailsDao.getRecipeById(1L) } returns null
        val apiResponse = RecipeDetailsResponse(
            id = 1L,
            title = "API Recipe",
            readyInMinutes = 15,
            servings = 2,
            summary = "From API",
            instructions = "Mix",
            vegetarian = true,
            vegan = false,
            glutenFree = false,
            dairyFree = false,
            image = "https://example.com/api.jpg",
            healthScore = 60.0,
            diets = null,
            spoonacularScore = 70.0,
            spoonacularSourceUrl = null,
            extendedIngredients = listOf(
                RecipeDetailsResponse.Ingredient(original = "1 cup flour", image = null)
            )
        )
        coEvery { recipeService.fetchRecipeDetails(1L) } returns ApiResult.Success(apiResponse)

        val result = repository.getRecipeDetailsById(1L)

        assertTrue(result is RecipeDetailsResult.Success)
        assertEquals("API Recipe", (result as RecipeDetailsResult.Success).recipeDetails.title)
        coVerify { recipeDetailsDao.upsert(any()) }
    }

    @Test
    fun `getRecipeDetailsById returns NoInternet when API fails with NoInternet`() = runTest {
        coEvery { recipeDetailsDao.getRecipeById(1L) } returns null
        coEvery { recipeService.fetchRecipeDetails(1L) } returns ApiResult.Failure(ApiError.NoInternet)

        val result = repository.getRecipeDetailsById(1L)

        assertEquals(RecipeDetailsResult.NoInternet, result)
    }

    // endregion

    // region fetchRecipeDetails

    @Test
    fun `fetchRecipeDetails returns Success and persists to database`() = runTest {
        val apiResponse = RecipeDetailsResponse(
            id = 1L,
            title = "Fresh Recipe",
            readyInMinutes = 20,
            servings = 2,
            summary = "Fresh",
            instructions = "Mix and serve",
            vegetarian = true,
            vegan = true,
            glutenFree = true,
            dairyFree = true,
            image = "https://example.com/fresh.jpg",
            healthScore = 90.0,
            diets = listOf("vegan"),
            spoonacularScore = 92.0,
            spoonacularSourceUrl = "https://spoonacular.com/2",
            extendedIngredients = listOf(
                RecipeDetailsResponse.Ingredient(original = "lettuce", image = null),
                RecipeDetailsResponse.Ingredient(original = "tomato", image = null)
            )
        )
        coEvery { recipeService.fetchRecipeDetails(1L) } returns ApiResult.Success(apiResponse)

        val result = repository.fetchRecipeDetails(1L)

        assertTrue(result is RecipeDetailsResult.Success)
        val details = (result as RecipeDetailsResult.Success).recipeDetails
        assertEquals("Fresh Recipe", details.title)
        assertEquals(listOf("lettuce", "tomato"), details.ingredients)
        coVerify { recipeDetailsDao.upsert(any()) }
    }

    @Test
    fun `fetchRecipeDetails returns NoInternet on API NoInternet error`() = runTest {
        coEvery { recipeService.fetchRecipeDetails(any()) } returns ApiResult.Failure(ApiError.NoInternet)

        val result = repository.fetchRecipeDetails(1L)

        assertEquals(RecipeDetailsResult.NoInternet, result)
    }

    @Test
    fun `fetchRecipeDetails returns Unknown on generic API error`() = runTest {
        coEvery { recipeService.fetchRecipeDetails(any()) } returns ApiResult.Failure(ApiError.HTTPError(500))

        val result = repository.fetchRecipeDetails(1L)

        assertEquals(RecipeDetailsResult.Unknown, result)
    }

    // endregion

    // region favoriteRecipes flow

    @Test
    fun `favoriteRecipes maps entities to domain models`() = runTest {
        val entities = listOf(
            FavoriteRecipeEntity(id = 1L, title = "Fav1", image = "https://example.com/1.jpg", summary = "S1"),
            FavoriteRecipeEntity(id = 2L, title = "Fav2", image = null, summary = "S2")
        )
        every { favoriteRecipeDao.getAll() } returns flowOf(entities)

        val repo = RecipeRepositoryImpl(
            recipeService = recipeService,
            recipesDao = recipesDao,
            recipeDetailsDao = recipeDetailsDao,
            favoriteRecipeDao = favoriteRecipeDao,
            recipePreferences = recipePreferences,
            ioDispatcher = testDispatcher
        )

        val recipes = mutableListOf<com.spitzer.domain.model.recipe.Recipe>()
        repo.favoriteRecipes.collect { recipes.addAll(it); return@collect }

        assertEquals(2, recipes.size)
        assertEquals("Fav1", recipes[0].title)
                assertNull(recipes[1].image)
    }

    // endregion
}
