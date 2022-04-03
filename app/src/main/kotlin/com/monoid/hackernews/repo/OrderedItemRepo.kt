package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import kotlinx.coroutines.flow.Flow

data class OrderedItem(
    val itemId: ItemId,
    val order: Int,
)

interface OrderedItemRepo {
    fun getRepoItems(): Flow<List<OrderedItem>>
    suspend fun updateRepoItems()
}
