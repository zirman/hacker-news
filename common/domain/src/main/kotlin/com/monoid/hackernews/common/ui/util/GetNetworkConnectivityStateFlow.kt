package com.monoid.hackernews.common.ui.util

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

fun getNetworkConnectivityStateFlow(
    coroutineScope: CoroutineScope,
    connectivityManager: ConnectivityManager,
): StateFlow<Boolean> =
    flow {
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
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )
