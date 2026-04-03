package com.spitzer.data.remote.common

import okio.IOException

sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T
    ) : ApiResult<T>()

    data class Failure(
        val error: ApiError
    ) : ApiResult<Nothing>()
}

sealed class ApiError : IOException() {
    data object NoInternet : ApiError() {
        @Suppress("unused")
        private fun readResolve(): Any = NoInternet
    }

    data object EmptyBody : ApiError() {
        @Suppress("unused")
        private fun readResolve(): Any = EmptyBody
    }

    data class HTTPError(
        val code: Int
    ) : ApiError()

    data class Unknown(
        val data: String? = null
    ) : ApiError()

    data class SpecificError(
        val httpCode: Int,
        val internalCode: Int,
        val value: String
    ) : ApiError()
}


