package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getTopStories
import com.monoid.hackernews.common.room.TopStoryDao
import com.monoid.hackernews.common.room.TopStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class TopStoryRepository(
    private val remoteDataSource: HttpClient,
    private val topStoryLocalDataSource: TopStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = topStoryLocalDataSource
        .getTopStories().map { topStories ->
            topStories.map {
                OrderedItem(
                    itemId = ItemId(it.itemId),
                    order = it.order
                )
            }
        }
        .shareIn(
            scope = scope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    override suspend fun updateItems() {
        topStoryLocalDataSource.replaceTopStories(
            remoteDataSource.getTopStories().mapIndexed { order, storyId ->
                TopStoryDb(
                    itemId = storyId,
                    order = order
                )
            }
        )
    }
}
