package com.monoid.hackernews.repo

import android.content.Context
import com.monoid.hackernews.R
import com.monoid.hackernews.ui.util.networkConnectivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit

abstract class OrderedItemRepo {
    fun getItems(context: Context): Flow<List<OrderedItem>> {
        return flow {
            coroutineScope {
                launch {
                    context.networkConnectivity(this).collectLatest { hasConnectivity ->
                        while (hasConnectivity) {
                            try {
                                updateDbItems()
                            } catch (error: Exception) {
                                if (error is CancellationException) throw error
                            }

                            delay(
                                TimeUnit.MINUTES.toMillis(
                                    context.resources.getInteger(R.integer.item_stale_minutes)
                                        .toLong()
                                )
                            )
                        }
                    }
                }

                emitAll(getDbItems())
            }
        }
    }

    protected abstract fun getDbItems(): Flow<List<OrderedItem>>

    protected abstract suspend fun updateDbItems()
}
