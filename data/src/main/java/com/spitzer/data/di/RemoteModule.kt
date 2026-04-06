package com.spitzer.data.di

import android.content.Context
import android.net.ConnectivityManager
import com.spitzer.data.BuildConfig
import com.spitzer.data.remote.common.ApiCaller
import com.spitzer.data.remote.common.ApiCallerImpl
import com.spitzer.data.remote.common.ConnectivityHandler
import com.spitzer.data.remote.common.ConnectivityHandlerImpl
import com.spitzer.data.remote.recipe.api.RecipeService
import com.spitzer.data.remote.recipe.api.RecipeServiceImpl
import com.spitzer.data.remote.recipe.api.SpoonacularService
import com.spitzer.data.remote.recipe.factory.HTTPClientFactory
import com.spitzer.data.remote.recipe.factory.HTTPClientFactoryImpl
import com.spitzer.data.remote.recipe.factory.ServiceFactory
import com.spitzer.data.remote.recipe.factory.ServiceFactoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkConfigurationModule {
    @Provides
    @Singleton
    internal fun provideHttpClientFactory(): HTTPClientFactory {
        return HTTPClientFactoryImpl()
    }

    @Provides
    @Singleton
    internal fun providesServiceFactory(
        clientFactory: HTTPClientFactory
    ): ServiceFactory {
        val baseUrl = BuildConfig.BASE_URL
        return ServiceFactoryImpl(httpClientFactory = clientFactory, baseUrl = baseUrl)
    }

    @Provides
    @Singleton
    internal fun providesConnectivityManager(
        @ApplicationContext applicationContext: Context
    ): ConnectivityManager {
        return applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}

@Module
@InstallIn(SingletonComponent::class)
class ServicesModule {
    @Provides
    @Singleton
    internal fun providesConnectivityHandler(
        connectivityManager: ConnectivityManager,
    ): ConnectivityHandler {
        return ConnectivityHandlerImpl(connectivityManager = connectivityManager)
    }

    @Provides
    @Singleton
    internal fun providesApiCaller(
        @ApplicationContext applicationContext: Context,
        connectivityHandler: ConnectivityHandler
    ): ApiCaller {
        return ApiCallerImpl(
            applicationContext = applicationContext,
            connectivityHandler = connectivityHandler
        )
    }

    @Provides
    @Singleton
    internal fun providesRecipeService(
        apiClientFactory: ServiceFactory,
        apiCaller: ApiCaller
    ): RecipeService {
        val apiService =
            apiClientFactory.provideRetrofitClient().create(SpoonacularService::class.java)
        return RecipeServiceImpl(apiService = apiService, apiCaller = apiCaller)
    }
}
