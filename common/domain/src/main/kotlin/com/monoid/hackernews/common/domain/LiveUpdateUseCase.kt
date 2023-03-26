package com.monoid.hackernews.common.domain

import android.net.ConnectivityManager
import android.util.Log
import com.monoid.hackernews.common.data.Repository
import com.monoid.hackernews.common.ui.util.getNetworkConnectivityStateFlow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LiveUpdateUseCase<out T>(
    private val connectivityManager: ConnectivityManager,
    private val repository: Repository<T>,
) {
    fun getItems(updatePeriod: Long): Flow<List<T>> = flow {
        coroutineScope {
            // start job to update periodically while flow is observed and there is network
            // connectivity
            getNetworkConnectivityStateFlow(
                coroutineScope = this,
                connectivityManager = connectivityManager
            )
                .onEach { hasConnectivity ->
                    while (hasConnectivity) {
                        try {
                            repository.updateItems()
                        } catch (error: Throwable) {
                            currentCoroutineContext().ensureActive()
                            Log.e(TAG, "getItems", error)
                        }

                        delay(updatePeriod)
                    }
                }
                .catch { Log.e(TAG, "getItems", it) }
                .launchIn(this)

            emitAll(repository.getItems(this))
        }
    }

    companion object {
        private const val TAG = "LiveUpdateUseCase"
    }
}
