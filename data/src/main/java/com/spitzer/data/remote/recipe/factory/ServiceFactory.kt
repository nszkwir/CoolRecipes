package com.spitzer.data.remote.recipe.factory

import retrofit2.Retrofit

interface ServiceFactory {
    fun provideRetrofitClient(): Retrofit
}

class ServiceFactoryImpl(
    private val httpClientFactory: HTTPClientFactory,
    private val baseUrl: String
) : ServiceFactory {
    override fun provideRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .client(httpClientFactory.provideOkHttpClient())
            .baseUrl(baseUrl)
            .addConverterFactory(JsonConverterFactory.getJsonConverter())
            .build()
    }
}
