package com.spitzer.data.remote.common

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.ConnectException
import javax.inject.Inject

internal interface ApiCaller {
    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        allowEmptyBody: Boolean = false,
        apiCall: suspend () -> Response<T>
    ): ApiResult<T>
}

/**
 * Implementation of [ApiCaller] that provides a robust wrapper for executing network requests.
 *
 * This class handles:
 * - Internet connectivity checks before initiating requests via [ConnectivityHandler].
 * - Exception handling for common network issues (e.g., [ConnectException]).
 * - Transformation of Retrofit [Response] objects into a structured [ApiResult].
 * - Support for optional empty response bodies.
 *
 * @property applicationContext The Android [Context] used within the implementation.
 * @property connectivityHandler Utility used to verify the current network status.
 */
internal data class ApiCallerImpl @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
    private val connectivityHandler: ConnectivityHandler
) : ApiCaller {
    override suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        allowEmptyBody: Boolean,
        apiCall: suspend () -> Response<T>
    ): ApiResult<T> {
        return withContext(dispatcher) {
            if (!connectivityHandler.hasInternetConnection()) {
                return@withContext ApiResult.Failure(ApiError.NoInternet)
            }
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        return@withContext ApiResult.Success(body)
                    } else if (allowEmptyBody) {
                        with(Unit as? T) {
                            if (this == null) {
                                return@withContext ApiResult.Failure(ApiError.EmptyBody)
                            } else {
                                return@withContext ApiResult.Success(this)
                            }
                        }
                    } else {
                        return@withContext ApiResult.Failure(ApiError.EmptyBody)
                    }
                } else {
                    // Here we could make validations to handle Specific errors
                    return@withContext ApiResult.Failure(ApiError.HTTPError(response.code()))
                }
            } catch (_: ConnectException) {
                return@withContext ApiResult.Failure(ApiError.NoInternet)
            } catch (e: Exception) {
                return@withContext ApiResult.Failure(ApiError.Unknown(e.message))
            }
        }
    }
}
