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

/**
 * Represents specialized network and API-related errors encountered during data operations.
 *
 * Extends [IOException] to integrate with standard network handling logic while providing
 * structured details about the failure cause.
 *
 * @property NoInternet Occurs when there is no active network connection.
 * @property EmptyBody Occurs when the server returns a successful status code but an empty response body.
 * @property HTTPError Represents a standard non-success HTTP status code.
 * @property Unknown An unexpected error occurred, potentially containing raw error data.
 * @property SpecificError A detailed error containing an HTTP code, a domain-specific internal code, and a descriptive value.
 */
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


