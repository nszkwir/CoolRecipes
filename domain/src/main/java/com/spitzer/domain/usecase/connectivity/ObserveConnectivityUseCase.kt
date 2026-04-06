package com.spitzer.domain.usecase.connectivity

import com.spitzer.domain.repository.ConnectivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectivityUseCase @Inject constructor(
    private val repository: ConnectivityRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.observeConnectivity()
    }
}
