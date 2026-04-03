package com.spitzer.data.remote.common

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal interface ConnectivityHandler {
    fun hasInternetConnection(): Boolean
}

internal class ConnectivityHandlerImpl @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context
) : ConnectivityHandler {
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun hasInternetConnection(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }
}
