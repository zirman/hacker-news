package com.monoid.hackernews.repo

import kotlinx.coroutines.flow.Flow

interface OrderedItemRepo {
    fun getRepoItems(): Flow<List<OrderedItem>>
    suspend fun updateRepoItems()
}
