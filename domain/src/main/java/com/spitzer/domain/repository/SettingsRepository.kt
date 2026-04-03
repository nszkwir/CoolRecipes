package com.spitzer.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun isDarkTheme(): Flow<Boolean>
    suspend fun setDarkTheme(isDark: Boolean)
}
