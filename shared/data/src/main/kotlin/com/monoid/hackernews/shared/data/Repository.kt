package com.monoid.hackernews.shared.data

import kotlinx.coroutines.flow.Flow

interface Repository<out T> {
    fun getItems(): Flow<List<T>>

    suspend fun updateItems()
}
