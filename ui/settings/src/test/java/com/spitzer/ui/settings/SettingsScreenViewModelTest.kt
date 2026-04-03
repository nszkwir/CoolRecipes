package com.spitzer.ui.settings

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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val settingsRepository: SettingsRepository = mockk()
    private val isDarkThemeFlow = MutableStateFlow(false)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { settingsRepository.isDarkTheme() } returns isDarkThemeFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SettingsScreenViewModel(settingsRepository)

    @Test
    fun `isDarkTheme initial value is false`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        assertEquals(false, viewModel.viewState.value.isDarkTheme)
    }

    @Test
    fun `isDarkTheme emits true when repository emits true`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        backgroundScope.launch {
            viewModel.viewState.collect()
        }
        isDarkThemeFlow.value = true
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.isDarkTheme)
    }

    @Test
    fun `isDarkTheme emits false when repository emits false`() = runTest(testDispatcher) {
        isDarkThemeFlow.value = true
        val viewModel = createViewModel()
        backgroundScope.launch {
            viewModel.viewState.collect()
        }
        advanceUntilIdle()

        isDarkThemeFlow.value = false
        advanceUntilIdle()

        assertFalse(viewModel.viewState.value.isDarkTheme)
    }

    @Test
    fun `setDarkTheme true delegates to repository`() = runTest(testDispatcher) {
        coEvery { settingsRepository.setDarkTheme(any()) } returns Unit
        val viewModel = createViewModel()

        viewModel.setDarkTheme(true)
        advanceUntilIdle()

        coVerify { settingsRepository.setDarkTheme(true) }
    }

    @Test
    fun `setDarkTheme false delegates to repository`() = runTest(testDispatcher) {
        coEvery { settingsRepository.setDarkTheme(any()) } returns Unit
        val viewModel = createViewModel()

        viewModel.setDarkTheme(false)
        advanceUntilIdle()

        coVerify { settingsRepository.setDarkTheme(false) }
    }
}
