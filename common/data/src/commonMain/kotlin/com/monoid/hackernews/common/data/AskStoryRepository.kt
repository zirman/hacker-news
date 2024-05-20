package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getAskStories
import com.monoid.hackernews.common.room.AskStoryDao
import com.monoid.hackernews.common.room.AskStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class AskStoryRepository(
    private val remoteDataSource: HttpClient,
    private val askStoryLocalDataSource: AskStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = askStoryLocalDataSource
        .getAskStories()
        .map { askStories ->
            askStories.map {
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
        askStoryLocalDataSource.replaceAskStories(
            remoteDataSource.getAskStories().mapIndexed { order, storyId ->
                AskStoryDb(
                    itemId = storyId,
                    order = order
                )
            }
        )
    }
}
