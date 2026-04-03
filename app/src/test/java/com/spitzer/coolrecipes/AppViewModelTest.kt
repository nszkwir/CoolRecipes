package com.spitzer.coolrecipes

import com.spitzer.domain.repository.RecipeRepository
import com.spitzer.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val settingsRepository: SettingsRepository = mockk()
    private val recipeRepository: RecipeRepository = mockk()
    private val isDarkThemeFlow = MutableStateFlow(false)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { settingsRepository.isDarkTheme() } returns isDarkThemeFlow
        coEvery { recipeRepository.initialize() } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AppViewModel(settingsRepository, recipeRepository)

    @Test
    fun `isDarkTheme initial value is false`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        assertFalse(viewModel.isDarkTheme.value)
    }

    @Test
    fun `isDarkTheme emits true when repository emits true`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        backgroundScope.launch {
            viewModel.isDarkTheme.collect()
        }
        advanceUntilIdle()

        isDarkThemeFlow.value = true
        advanceUntilIdle()
        assertTrue(viewModel.isDarkTheme.value)
    }

    @Test
    fun `init calls recipeRepository initialize`() = runTest(testDispatcher) {
        createViewModel()
        advanceUntilIdle()

        coVerify { recipeRepository.initialize() }
    }

    @Test
    fun `isDarkTheme reflects repository changes`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        backgroundScope.launch {
            viewModel.isDarkTheme.collect()
        }
        advanceUntilIdle()

        isDarkThemeFlow.value = true
        advanceUntilIdle()
        assertTrue(viewModel.isDarkTheme.value)

        isDarkThemeFlow.value = false
        advanceUntilIdle()
        assertFalse(viewModel.isDarkTheme.value)
    }
}
