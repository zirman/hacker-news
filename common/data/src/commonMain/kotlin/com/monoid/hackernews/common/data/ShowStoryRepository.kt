package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getShowStories
import com.monoid.hackernews.common.room.ShowStoryDao
import com.monoid.hackernews.common.room.ShowStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class ShowStoryRepository(
    private val httpClient: HttpClient,
    private val showStoryLocalDataSource: ShowStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = showStoryLocalDataSource
        .getShowStories()
        .map { showStories ->
            showStories.map {
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
        showStoryLocalDataSource.replaceShowStories(
            httpClient.getShowStories().mapIndexed { order, storyId ->
                ShowStoryDb(
                    itemId = storyId,
                    order = order
                )
            }
        )
    }
}
