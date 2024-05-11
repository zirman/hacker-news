package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getNewStories
import com.monoid.hackernews.common.room.NewStoryDao
import com.monoid.hackernews.common.room.NewStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class NewStoryRepository(
    private val httpClient: HttpClient,
    private val newStoryDao: NewStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> =
        newStoryDao.getNewStories()
            .map { topStories ->
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
        newStoryDao.replaceNewStories(
            httpClient.getNewStories().mapIndexed { order, storyId ->
                NewStoryDb(
                    itemId = storyId,
                    order = order
                )
            }
        )
    }
}
