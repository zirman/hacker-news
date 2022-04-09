package com.monoid.hackernews.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

fun Context.networkConnectivity(): Flow<Boolean> =
    flow {
        val connectivityManager: ConnectivityManager =
            getSystemService()!!

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val hasConnectivityState =
            MutableStateFlow(false)

        val networkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    hasConnectivityState.tryEmit(true)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    hasConnectivityState.tryEmit(false)
                }
            }

        connectivityManager.requestNetwork(networkRequest, networkCallback)

        try {
            emitAll(hasConnectivityState)
        } finally {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

suspend fun <T> Flow<T>.runWhen(predicate: (T) -> Boolean, block: suspend () -> Unit) {
    collectLatest {
        if (predicate(it)) {
            block()
        }
    }
}
