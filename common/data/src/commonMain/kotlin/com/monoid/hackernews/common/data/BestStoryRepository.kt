package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getBestStories
import com.monoid.hackernews.common.room.BestStoryDao
import com.monoid.hackernews.common.room.BestStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class BestStoryRepository(
    private val remoteDataSource: HttpClient,
    private val bestStoryLocalDataSource: BestStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = bestStoryLocalDataSource
        .getBestStories()
        .map { bestStories ->
            bestStories.map {
                OrderedItem(
                    itemId = ItemId(it.itemId),
                    order = it.order,
                )
            }
        }
        .shareIn(
            scope = scope,
            started = SharingStarted.Lazily,
            replay = 1,
        )

    override suspend fun updateItems() {
        bestStoryLocalDataSource.replaceBestStories(
            remoteDataSource.getBestStories().mapIndexed { order, storyId ->
                BestStoryDb(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
