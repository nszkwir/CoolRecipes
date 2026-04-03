package com.spitzer.data.repository.settings

import com.spitzer.data.local.settings.SettingsDataStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRepositoryImplTest {

    private val settingsDataStore: SettingsDataStore = mockk()
    private val repository = SettingsRepositoryImpl(settingsDataStore)

    @Test
    fun `isDarkTheme returns flow from data store`() = runTest {
        val flow = MutableStateFlow(true)
        every { settingsDataStore.isDarkTheme } returns flow

        val result = repository.isDarkTheme().first()

        assertTrue(result)
        verify { settingsDataStore.isDarkTheme }
    }

    @Test
    fun `isDarkTheme returns false when data store returns false`() = runTest {
        val flow = MutableStateFlow(false)
        every { settingsDataStore.isDarkTheme } returns flow

        val result = repository.isDarkTheme().first()

        assertFalse(result)
    }

    @Test
    fun `isDarkTheme emits updated values`() = runTest {
        val flow = MutableStateFlow(false)
        every { settingsDataStore.isDarkTheme } returns flow

        assertFalse(repository.isDarkTheme().first())

        flow.value = true
        assertTrue(repository.isDarkTheme().first())
    }

    @Test
    fun `setDarkTheme delegates to data store with true`() = runTest {
        coEvery { settingsDataStore.setDarkTheme(any()) } returns Unit

        repository.setDarkTheme(true)

        coVerify { settingsDataStore.setDarkTheme(true) }
    }

    @Test
    fun `setDarkTheme delegates to data store with false`() = runTest {
        coEvery { settingsDataStore.setDarkTheme(any()) } returns Unit

        repository.setDarkTheme(false)

        coVerify { settingsDataStore.setDarkTheme(false) }
    }
}
