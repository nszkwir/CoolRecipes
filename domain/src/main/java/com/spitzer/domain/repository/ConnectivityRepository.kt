package com.spitzer.domain.repository

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    fun hasInternetConnection(): Boolean
    fun observeConnectivity(): Flow<Boolean>
}
