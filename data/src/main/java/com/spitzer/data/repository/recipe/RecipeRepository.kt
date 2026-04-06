package com.spitzer.data.repository.recipe

import com.spitzer.data.local.recipe.dao.FavoriteRecipeDao
import com.spitzer.data.local.recipe.dao.RecipeDetailsDao
import com.spitzer.data.local.recipe.dao.RecipesDao
import com.spitzer.data.local.recipe.entities.FavoriteRecipeEntity
import com.spitzer.data.local.recipe.sharedpreferences.RecipeSharedPreferences
import com.spitzer.data.pagination.OffsetPaginator
import com.spitzer.data.pagination.PageFetchResult
import com.spitzer.data.pagination.PageResponse
import com.spitzer.data.remote.common.ApiError
import com.spitzer.data.remote.common.ApiResult
import com.spitzer.data.remote.recipe.api.RecipeService
import com.spitzer.data.repository.recipe.mapper.RecipeDetailsMapper.mapFromRecipeDetailsResponse
import com.spitzer.data.repository.recipe.mapper.RecipeDetailsMapper.mapFromStoredRecipeDetails
import com.spitzer.data.repository.recipe.mapper.RecipeDetailsMapper.mapToStoredRecipeDetails
import com.spitzer.data.repository.recipe.mapper.RecipeMapper.mapFromFavoriteEntity
import com.spitzer.data.repository.recipe.mapper.RecipeMapper.mapFromRecipeResponse
import com.spitzer.data.repository.recipe.mapper.RecipeMapper.mapFromStoredRecipe
import com.spitzer.data.repository.recipe.mapper.RecipeMapper.mapSortCriteria
import com.spitzer.data.repository.recipe.mapper.RecipeMapper.mapSortOrder
import com.spitzer.data.repository.recipe.mapper.RecipeMapper.mapToStoredRecipes
import com.spitzer.domain.model.pagination.IndexPaginator
import com.spitzer.domain.model.pagination.PaginationResult
import com.spitzer.domain.model.recipe.Recipe
import com.spitzer.domain.model.recipe.RecipePage
import com.spitzer.domain.model.recipe.RecipeSearchCriteria
import com.spitzer.domain.model.recipe.RecipeSortCriteria
import com.spitzer.domain.model.recipe.RecipeSortOrder
import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.usecase.favorites.result.SetRecipeFavoriteStatusResult
import com.spitzer.domain.usecase.recipe.result.RecipePaginationResult
import com.spitzer.domain.usecase.recipe.result.SearchRecipeResult
import com.spitzer.domain.usecase.recipedetails.result.RecipeDetailsResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeService: RecipeService,
    private val recipesDao: RecipesDao,
    private val recipeDetailsDao: RecipeDetailsDao,
    private val favoriteRecipeDao: FavoriteRecipeDao,
    private val recipePreferences: RecipeSharedPreferences,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RecipeRepository {

    companion object {
        const val RECIPES_PAGE_LIMIT = 15
    }

    /**
     * Current sort criteria used by the paginator's fetch function.
     * Updated before each paginator call (refresh/fetch).
     */
    private var currentSortCriteria: RecipeSortCriteria = RecipeSortCriteria.RELEVANCE
    private var currentSortOrder: RecipeSortOrder = RecipeSortOrder.DESCENDING

    /**
     * Reusable [IndexPaginator] encapsulating all offset-based pagination logic.
     * Fetches [Recipe] items from the API and persists them to the local DB on each page load.
     */
    private val recipePaginator: IndexPaginator<Recipe> = OffsetPaginator(
        pageSize = RECIPES_PAGE_LIMIT,
        fetchPage = { offset, limit -> fetchRecipePage(offset, limit) },
        onPageLoaded = { items, _ ->
            recipesDao.upsert(mapToStoredRecipes(items))
        }
    )

    private val _recipePage: MutableStateFlow<RecipePage> =
        MutableStateFlow(RecipePage(list = mutableListOf(), totalResults = 0))

    override val recipePage: StateFlow<RecipePage> = _recipePage.asStateFlow()

    override val favoriteRecipes: Flow<List<Recipe>> =
        favoriteRecipeDao.getAll()
            .map { entities ->
                entities.map { mapFromFavoriteEntity(it) }
            }
    override val favoriteRecipeIds: Flow<Set<Long>> =
        favoriteRecipes.map { list ->
            list.map { it.id }.toSet()
        }

    override suspend fun initialize() {
        withContext(ioDispatcher) {
            try {
                val recipes = recipesDao.get().map {
                    mapFromStoredRecipe(it)
                }

                val totalResults = recipePreferences.getRecipeListTotalResults()

                // Allocation of the recipe list to allow infinite scrolling
                val recipeList: MutableList<Recipe?> =
                    MutableList(totalResults) { null }

                recipes.forEachIndexed { index, recipe ->
                    recipeList[index] = recipe
                }

                _recipePage.value = RecipePage(
                    list = recipeList,
                    totalResults = totalResults
                )
            } catch (_: Exception) {
                initializeEmptyRecipes()
            }
        }
    }

    private fun initializeEmptyRecipes() {
        _recipePage.value = RecipePage(
            list = mutableListOf(),
            totalResults = 0
        )
        recipePreferences.updateRecipeListTotalResults(0)
    }

    /**
     * Updates the favorite value for a recipe.
     * The update is performed in the local favoriteRecipes map, on database.
     * On our main recipe list state _recipePage, we iterate also to update the recipe favorite value.
     */
    override suspend fun setRecipeFavorite(
        id: Long,
        isFavorite: Boolean,
        title: String,
        image: String?,
        summary: String
    ): SetRecipeFavoriteStatusResult {
        if (isFavorite) {
            favoriteRecipeDao.upsert(
                FavoriteRecipeEntity(
                    id = id,
                    title = title,
                    image = image,
                    summary = summary
                )
            )
        } else {
            favoriteRecipeDao.deleteById(id)
        }
        return SetRecipeFavoriteStatusResult.Success
    }

    /**
     * Fetches the first recipe page by refreshing the paginator.
     */
    override suspend fun refreshRecipeList(
        sortCriteria: RecipeSortCriteria,
        sortOrder: RecipeSortOrder
    ): RecipePaginationResult {
        currentSortCriteria = sortCriteria
        currentSortOrder = sortOrder
        val result = recipePaginator.refresh()
        when (result) {
            is PaginationResult.Success -> syncPaginatorState()
            else -> Unit
        }
        return mapPaginationResult(result)
    }

    /**
     * Fetches the next recipes page if the given element index requires it.
     */
    override suspend fun fetchRecipeList(
        elementIndex: Int,
        sortCriteria: RecipeSortCriteria,
        sortOrder: RecipeSortOrder
    ): RecipePaginationResult {
        currentSortCriteria = sortCriteria
        currentSortOrder = sortOrder
        val result = recipePaginator.loadNextPage(elementIndex)
        when (result) {
            is PaginationResult.Success -> syncPaginatorState()
            else -> Unit
        }
        return mapPaginationResult(result)
    }

    /**
     * Syncs the paginator's internal state into the repository's [_recipePage] StateFlow,
     */
    private fun syncPaginatorState() {
        val paginatedData = recipePaginator.data.value
        recipePreferences.updateRecipeListTotalResults(paginatedData.totalItems)
        _recipePage.update {
            RecipePage(
                list = paginatedData.items,
                totalResults = paginatedData.totalItems
            )
        }
    }

    /**
     * Maps the generic [PaginationResult] to the domain-specific [RecipePaginationResult].
     */
    private fun mapPaginationResult(result: PaginationResult): RecipePaginationResult {
        return when (result) {
            PaginationResult.Success -> RecipePaginationResult.Success
            PaginationResult.Empty -> RecipePaginationResult.Empty
            PaginationResult.AlreadyLoaded -> RecipePaginationResult.WrongIndex
            PaginationResult.NoInternet -> RecipePaginationResult.NoInternet
            PaginationResult.Error -> RecipePaginationResult.Unknown
        }
    }

    /**
     * Fetches a single page of recipes from the API.
     * Used as the [OffsetPaginator]'s fetch function.
     */
    private suspend fun fetchRecipePage(
        offset: Int,
        limit: Int
    ): PageFetchResult<Recipe> {
        val apiResult = recipeService.fetchRecipes(
            offset = offset,
            limit = limit,
            query = null,
            includeIngredients = null,
            sortCriteria = mapSortCriteria(currentSortCriteria),
            sortOrder = mapSortOrder(currentSortOrder)
        )

        return when (apiResult) {
            is ApiResult.Failure -> when (apiResult.error) {
                is ApiError.NoInternet -> PageFetchResult.NoInternet
                else -> PageFetchResult.Error
            }

            is ApiResult.Success -> {
                val recipes = apiResult.data.results.map { response ->
                    mapFromRecipeResponse(response)
                }
                PageFetchResult.Success(
                    PageResponse(
                        items = recipes,
                        totalItems = apiResult.data.totalResults
                    )
                )
            }
        }
    }

    /**
     * Request the recipes search by criteria.
     */
    override suspend fun searchRecipeList(
        query: String,
        searchCriteria: RecipeSearchCriteria,
        sortCriteria: RecipeSortCriteria,
        sortOrder: RecipeSortOrder
    ): SearchRecipeResult {

        val searchByName = if (searchCriteria == RecipeSearchCriteria.NAME) query
        else null
        val searchByIngredients = if (searchCriteria == RecipeSearchCriteria.INGREDIENTS) {
            query.split("[\\s,]+".toRegex()).joinToString(",")
        } else null
        val apiResult = recipeService.fetchRecipes(
            offset = 0,
            limit = 20,
            query = searchByName,
            includeIngredients = searchByIngredients,
            sortCriteria = mapSortCriteria(sortCriteria),
            sortOrder = mapSortOrder(sortOrder)
        )

        return when (apiResult) {
            is ApiResult.Failure -> when (apiResult.error) {
                is ApiError.NoInternet -> {
                    SearchRecipeResult.NoInternet
                }

                else -> {
                    SearchRecipeResult.Unknown
                }
            }

            is ApiResult.Success -> {
                val recipes = apiResult.data.results.map {
                    mapFromRecipeResponse(it)
                }
                SearchRecipeResult.Success(recipes)
            }
        }
    }

    /**
     *  Get the recipeDetails by recipe id.
     */
    override suspend fun getRecipeDetailsById(id: Long): RecipeDetailsResult {
        // Search the recipe details on database
        val recipeDetailsEntity = recipeDetailsDao.getRecipeById(id)

        return if (recipeDetailsEntity != null) {
            val recipeDetails = mapFromStoredRecipeDetails(recipeDetailsEntity)
            RecipeDetailsResult.Success(recipeDetails)
        } else {
            // If not found on database, will fetch from
            // remote and update the result on database.
            fetchRecipeDetails(id)
        }
    }

    /**
     *  Fetches the recipeDetails by recipe id from remote.
     */
    override suspend fun fetchRecipeDetails(id: Long): RecipeDetailsResult {
        return when (val apiResult = recipeService.fetchRecipeDetails(id)) {
            is ApiResult.Failure -> when (apiResult.error) {
                is ApiError.NoInternet -> {
                    RecipeDetailsResult.NoInternet
                }

                else -> {
                    RecipeDetailsResult.Unknown
                }
            }

            is ApiResult.Success -> {
                val recipeDetails = mapFromRecipeDetailsResponse(
                    apiResult.data
                )
                val recipeDetailsEntity = mapToStoredRecipeDetails(recipeDetails)
                recipeDetailsDao.upsert(recipeDetailsEntity)
                RecipeDetailsResult.Success(recipeDetails)
            }
        }
    }
}
