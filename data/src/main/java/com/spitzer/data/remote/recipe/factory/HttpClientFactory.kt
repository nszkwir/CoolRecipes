package com.spitzer.data.remote.recipe.factory

import com.spitzer.data.remote.recipe.interceptor.APIKeyRequestInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

interface HTTPClientFactory {
    fun provideOkHttpClient(): OkHttpClient
}

class HTTPClientFactoryImpl() : HTTPClientFactory {
    override fun provideOkHttpClient(): OkHttpClient {
        val timeout: Long = 30
        return OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor(APIKeyRequestInterceptor)
            .build()
    }
}
