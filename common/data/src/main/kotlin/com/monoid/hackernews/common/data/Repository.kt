package com.monoid.hackernews.common.data

import kotlinx.coroutines.flow.Flow

interface Repository<out T> {
    val items: Flow<List<T>>
    suspend fun updateItems()
}
