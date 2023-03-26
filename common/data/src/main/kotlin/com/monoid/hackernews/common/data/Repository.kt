package com.monoid.hackernews.common.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface Repository<out T> {
    fun getItems(scope: CoroutineScope): Flow<List<T>>
    suspend fun updateItems()
}
