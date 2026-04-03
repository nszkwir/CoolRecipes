package com.spitzer.data.repository.settings

import com.spitzer.data.local.settings.SettingsDataStore
import com.spitzer.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override fun isDarkTheme(): Flow<Boolean> {
        return settingsDataStore.isDarkTheme
    }

    override suspend fun setDarkTheme(isDark: Boolean) {
        settingsDataStore.setDarkTheme(isDark)
    }
}