package com.monoid.hackernews.data

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getNewStories
import com.monoid.hackernews.room.NewStoryDao
import com.monoid.hackernews.room.NewStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewStoryRepository(
    private val httpClient: HttpClient,
    private val newStoryDao: NewStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
        return newStoryDao.getNewStories()
            .map { topStories ->
                topStories.map {
                    OrderedItem(
                        itemId = ItemId(it.itemId),
                        order = it.order,
                    )
                }
            }
    }

    override suspend fun updateItems() {
        newStoryDao.replaceNewStories(
            httpClient.getNewStories().mapIndexed { order, storyId ->
                NewStoryDb(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
