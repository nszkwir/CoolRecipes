package com.spitzer.ui.recipes.screen.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.spitzer.ui.recipes.screen.list.views.RecipeListBottomSheetViewState
import com.spitzer.ui.recipes.screen.list.views.RecipeListScreenSearchBarViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecipeListScreenViewModelOutput {
    data class RecipeDetail(val recipeId: Long) : RecipeListScreenViewModelOutput()
}

@HiltViewModel
class RecipeListScreenViewModel @Inject constructor(
    private val getRecipeListUseCase: GetRecipeListUseCase,
    private val refreshRecipeListUseCase: RefreshRecipeListUseCase,
    private val fetchNextRecipePageWhenNeededUseCase: FetchNextRecipePageWhenNeededUseCase,
    private val searchRecipeListUseCase: SearchRecipePageUseCase
) : ViewModel() {

    // Variables that allow handling logic to skip updates when the search criteria doesn't change
    private var lastSearchCriteria: RecipeSearchCriteria = RecipeSearchCriteria.NAME
    private var lastSortOrder: RecipeSortOrder = RecipeSortOrder.DESCENDING
    private var lastSortCriteria: RecipeSortCriteria = RecipeSortCriteria.RELEVANCE

    companion object {
        const val REFRESH_DELAY: Long = 5_000
    }

    lateinit var output: (RecipeListScreenViewModelOutput) -> Unit

    private val _viewState by lazy {
        MutableStateFlow(
            RecipeListScreenViewState(
                recipeList = emptyList(),
                isLoading = true,
                searchBarViewState = RecipeListScreenSearchBarViewState(
                    isSearchActive = false,
                    isLoading = false,
                    query = "",
                    isFunnelOn = false,
                    isFunnelEnabled = true,
                    searchCriteria = lastSearchCriteria,
                    recipesList = emptyList()
                ),
                bottomSheetViewState = RecipeListBottomSheetViewState(
                    selectedSearchCriteria = lastSearchCriteria,
                    selectedSortOrder = lastSortOrder,
                    selectedSortCriteria = lastSortCriteria,
                    searchCriteriaList = RecipeSearchCriteria.entries,
                    sortCriteriaList = RecipeSortCriteria.entries,
                    sortOrderList = RecipeSortOrder.entries,
                    shouldHide = true
                ),
                message = null
            )
        )
    }
    val viewState: StateFlow<RecipeListScreenViewState> by lazy {
        _viewState.asStateFlow()
    }

    private var retryJob: Job? = null
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            getRecipeListUseCase().collectLatest { recipePage ->
                inputChanged(recipePage)
            }
        }

        refreshRecipeList()
    }

    /**
     * Updates the view state with the list of recipes contained within the provided [recipePage].
     * This method is called whenever the underlying recipe data source emits a new page.
     *
     * @param recipePage The [RecipePage] containing the updated list of recipes to be displayed.
     */
    private fun inputChanged(recipePage: RecipePage) {
        _viewState.update { currentState ->
            currentState.copy(
                recipeList = recipePage.list
            )
        }
    }

    /**
     * Holds a reference to the action that failed most recently.
     * This allows the UI to dynamically assign the correct retry logic to the
     * primary button of an error message or dialog.
     */
    private var lastFailedAction: (() -> Unit)? = null

    fun onFunnelTap() {
        _viewState.update { currentState ->
            currentState.copy(
                bottomSheetViewState = currentState.bottomSheetViewState.copy(
                    shouldHide = false
                )
            )
        }
    }

    fun onQueryChange(query: String) {
        _viewState.update { currentState ->
            currentState.copy(
                searchBarViewState = currentState.searchBarViewState.copy(
                    query = query
                )
            )
        }
        searchRecipeList()
    }

    fun onOpenSearch() {
        _viewState.update { currentState ->
            currentState.copy(
                searchBarViewState = currentState.searchBarViewState.copy(
                    isSearchActive = true
                )
            )
        }
    }

    fun onCloseSearch() {
        _viewState.update { currentState ->
            currentState.copy(
                searchBarViewState = currentState.searchBarViewState.copy(
                    isSearchActive = false,
                    query = "",
                    recipesList = emptyList()
                )
            )
        }
    }


    fun onSearchCriteriaSelected(searchCriteria: RecipeSearchCriteria) {
        _viewState.update { currentState ->
            currentState.copy(
                bottomSheetViewState = currentState.bottomSheetViewState.copy(
                    selectedSearchCriteria = searchCriteria
                ),
                searchBarViewState = currentState.searchBarViewState.copy(
                    searchCriteria = searchCriteria
                )
            )
        }
    }

    fun onSortCriteriaSelected(sortCriteria: RecipeSortCriteria) {
        _viewState.update { currentState ->
            currentState.copy(
                bottomSheetViewState = currentState.bottomSheetViewState.copy(
                    selectedSortCriteria = sortCriteria
                )
            )
        }
    }

    fun onSortOrderSelected(sortOrder: RecipeSortOrder) {
        _viewState.update { currentState ->
            currentState.copy(
                bottomSheetViewState = currentState.bottomSheetViewState.copy(
                    selectedSortOrder = sortOrder
                )
            )
        }
    }

    fun clearSearchFilters() {
        _viewState.update { currentState ->
            currentState.copy(
                bottomSheetViewState = currentState.bottomSheetViewState.copy(
                    selectedSearchCriteria = RecipeSearchCriteria.NAME,
                    selectedSortOrder = RecipeSortOrder.DESCENDING,
                    selectedSortCriteria = RecipeSortCriteria.RELEVANCE
                ),
                searchBarViewState = currentState.searchBarViewState.copy(
                    searchCriteria = RecipeSearchCriteria.NAME
                )
            )
        }
        filterChanged()
    }

    fun confirmSearchFilters() {
        filterChanged()
    }

    fun onDismissBottomSheet() {
        filterChanged()
    }

    private fun filterChanged() {
        // Check if criteria has changed to avoid unnecessary updates
        if (criteriaHasChanged()) {
            // Each time the filter changes we update the funnel state
            updateFunnelState()
            if (_viewState.value.searchBarViewState.isSearchActive) {
                searchRecipeList()
            } else {
                refreshRecipeList()
            }
        }
        removeBottomSheet()
    }

    private fun updateFunnelState() {
        _viewState.update { currentState ->
            currentState.copy(
                searchBarViewState = currentState.searchBarViewState.copy(
                    isFunnelOn = isFunnelOn()
                )
            )
        }
    }

    // The funnel is on when any of the criteria are different from the default values
    private fun isFunnelOn(): Boolean {
        return with(_viewState.value.bottomSheetViewState) {
            !(this.selectedSearchCriteria == RecipeSearchCriteria.NAME &&
                    this.selectedSortCriteria == RecipeSortCriteria.RELEVANCE &&
                    this.selectedSortOrder == RecipeSortOrder.DESCENDING)
        }
    }

    private fun removeBottomSheet() {
        _viewState.update { currentState ->
            currentState.copy(
                bottomSheetViewState = currentState.bottomSheetViewState.copy(
                    shouldHide = true
                )
            )
        }
    }

    private fun updateLastSearchCriteria() {
        with(_viewState.value.bottomSheetViewState) {
            lastSearchCriteria = this.selectedSearchCriteria
            lastSortCriteria = this.selectedSortCriteria
            lastSortOrder = this.selectedSortOrder
        }
    }

    // Checks if the search criteria has changed comparing to last values
    private fun criteriaHasChanged(): Boolean {
        return with(_viewState.value.bottomSheetViewState) {
            lastSearchCriteria != this.selectedSearchCriteria ||
                    lastSortCriteria != this.selectedSortCriteria ||
                    lastSortOrder != this.selectedSortOrder
        }
    }

    /**
     * Executes a recipe search based on the current query and filter criteria.
     *
     * The search is only triggered if the query length is greater than 2 characters.
     * It manages search state by:
     * 1. Showing a loading animation.
     * 2. Cancelling any existing search jobs to prevent race conditions.
     * 3. Launching a new coroutine to fetch data via [searchRecipeListUseCase].
     * 4. Handling result states: updating the UI on success or showing error messages
     *    (No Internet/Unknown) with retry logic.
     *
     * If the query is empty, it clears the current search results and cancels pending jobs.
     */
    private fun searchRecipeList() {
        val query = _viewState.value.searchBarViewState.query

        // Won't trigger search unless the search query string is longer than 2 characters
        if (query.trim().length > 2) {
            showSearchLoadingAnimation()
            updateLastSearchCriteria()
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                val result = searchRecipeListUseCase(
                    query = query,
                    searchCriteria = _viewState.value.bottomSheetViewState.selectedSearchCriteria,
                    sortCriteria = _viewState.value.bottomSheetViewState.selectedSortCriteria,
                    sortOrder = _viewState.value.bottomSheetViewState.selectedSortOrder
                )
                when (result) {
                    SearchRecipeResult.NoInternet -> {
                        showNoInternetConnectionError(
                            retryAction = {
                                searchRecipeList()
                            }
                        )
                    }

                    SearchRecipeResult.Unknown -> {
                        showGenericError(
                            retryAction = {
                                searchRecipeList()
                            }
                        )
                    }

                    is SearchRecipeResult.Success -> {
                        _viewState.update { currentState ->
                            currentState.copy(
                                searchBarViewState = currentState.searchBarViewState.copy(
                                    recipesList = result.recipeList
                                )
                            )
                        }
                    }
                }
                hideSearchLoadingAnimation()
            }
        } else if (query.trim().isEmpty()) {
            searchJob?.cancel()
            _viewState.update { currentState ->
                currentState.copy(
                    searchBarViewState = currentState.searchBarViewState.copy(
                        recipesList = emptyList()
                    )
                )
            }
        }
    }

    fun onRecipeCardClicked(recipe: Recipe) {
        output(RecipeListScreenViewModelOutput.RecipeDetail(recipe.id))
    }

    /**
     * Refreshes the recipe list by fetching the first page of results from the server
     * using the current sort criteria and order defined in the bottom sheet state.
     *
     * This function performs the following steps:
     * 1. Displays a loading animation.
     * 2. Synchronizes the last used search criteria with the current UI state.
     * 3. Executes the [refreshRecipeListUseCase] within the [viewModelScope].
     * 4. Hides the loading animation upon completion.
     * 5. Handles errors (No Internet or Generic) by showing an error message if the
     *    existing recipe list is empty, providing a retry mechanism that calls this function again.
     */
    fun refreshRecipeList() {
        showLoadingAnimation()
        updateLastSearchCriteria()
        viewModelScope.launch {
            val result = refreshRecipeListUseCase(
                sortCriteria = _viewState.value.bottomSheetViewState.selectedSortCriteria,
                sortOrder = _viewState.value.bottomSheetViewState.selectedSortOrder
            )
            hideLoadingAnimation()
            when (result) {
                RecipePaginationResult.Unknown,
                RecipePaginationResult.WrongIndex,
                RecipePaginationResult.Empty -> {
                    if (_viewState.value.recipeList.isEmpty()) {
                        showGenericError(
                            retryAction = {
                                refreshRecipeList()
                            }
                        )
                    }
                }

                RecipePaginationResult.NoInternet -> {
                    if (_viewState.value.recipeList.isEmpty()) {
                        showNoInternetConnectionError(
                            retryAction = {
                                refreshRecipeList()
                            }
                        )
                    }
                }

                RecipePaginationResult.Success -> {
                    // nothing
                }
            }
        }
    }

    /**
     * Fetches the next page of recipes based on the current scroll position.
     *
     * This function is typically called when the user scrolls through the list. It triggers
     * a request to load more data only when the [elementIndex] indicates that the end of
     * the current list is being approached.
     *
     * @param elementIndex The index of the item currently being rendered or accessed in the list,
     * used to determine if a new page should be fetched.
     */
    fun getRecipeList(elementIndex: Int) {
        retryJob?.cancel()
        viewModelScope.launch {
            val result = fetchNextRecipePageWhenNeededUseCase(
                elementIndex = elementIndex,
                sortCriteria = _viewState.value.bottomSheetViewState.selectedSortCriteria,
                sortOrder = _viewState.value.bottomSheetViewState.selectedSortOrder,
            )
            when (result) {
                RecipePaginationResult.NoInternet -> {
                    showNoInternetConnectionError(
                        retryAction = {
                            handleRetryError(elementIndex)
                        }
                    )
                }

                RecipePaginationResult.Unknown,
                RecipePaginationResult.WrongIndex,
                RecipePaginationResult.Empty,
                RecipePaginationResult.Success -> {
                    // nothing
                }
            }
        }
    }

    private fun handleRetryError(elementIndex: Int) {
        retryJob = viewModelScope.launch {
            delay(REFRESH_DELAY)
            getRecipeList(elementIndex = elementIndex)
        }
    }

    /** Loading States */
    private fun showSearchLoadingAnimation() {
        _viewState.update {
            it.copy(
                searchBarViewState = it.searchBarViewState.copy(
                    isLoading = true
                )
            )
        }
    }

    private fun hideSearchLoadingAnimation() {
        _viewState.update {
            it.copy(
                searchBarViewState = it.searchBarViewState.copy(
                    isLoading = false
                )
            )
        }
    }

    private fun showLoadingAnimation() {
        _viewState.update { currentState ->
            currentState.copy(
                message = null,
                isLoading = true
            )
        }
    }

    private fun hideLoadingAnimation() {
        _viewState.update { currentState ->
            currentState.copy(
                isLoading = false
            )
        }
    }

    /** Error messages */
    fun showNoInternetConnectionError(
        retryAction: (() -> Unit)? = null
    ) {
        lastFailedAction = retryAction
        _viewState.update { currentState ->
            currentState.copy(
                message = RecipeListScreenViewState.Message(
                    type = RecipeListScreenViewState.Message.Type.NO_INTERNET
                )
            )
        }
    }

    fun showGenericError(
        retryAction: (() -> Unit)? = null
    ) {
        lastFailedAction = retryAction
        _viewState.update { currentState ->
            currentState.copy(
                message = RecipeListScreenViewState.Message(
                    type = RecipeListScreenViewState.Message.Type.GENERIC
                )
            )
        }
    }

    fun onMessagePrimaryButtonClicked() {
        lastFailedAction?.invoke()
        lastFailedAction = null
        removeErrorView()
    }

    fun onMessageSecondaryButtonClicked() {
        lastFailedAction = null
        removeErrorView()
    }

    fun removeErrorView() {
        _viewState.update { currentState ->
            currentState.copy(
                message = null
            )
        }
    }
}
