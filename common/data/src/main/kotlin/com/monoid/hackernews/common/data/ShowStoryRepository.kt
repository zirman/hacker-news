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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowStoryRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val showStoryDao: ShowStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = showStoryDao
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
        showStoryDao.replaceShowStories(
            httpClient.getShowStories().mapIndexed { order, storyId ->
                ShowStoryDb(
                    itemId = storyId,
                    order = order
                )
            }
        )
    }
}
