package com.spitzer.data.repository.connectivity

import com.spitzer.data.remote.common.ConnectivityHandler
import com.spitzer.domain.repository.ConnectivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectivityRepositoryImpl @Inject constructor(
    private val connectivityHandler: ConnectivityHandler
) : ConnectivityRepository {

    override fun observeConnectivity(): Flow<Boolean> {
        return connectivityHandler.observeConnectivity()
    }

    override fun hasInternetConnection(): Boolean {
        return connectivityHandler.hasInternetConnection()
    }
}
