package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getNewStories
import com.monoid.hackernews.common.room.NewStoryDao
import com.monoid.hackernews.common.room.NewStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewStoryRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val newStoryDao: NewStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
        return newStoryDao.getNewStories().map { topStories ->
            topStories.map {
                OrderedItem(
                    itemId = ItemId(it.itemId),
                    order = it.order
                )
            }
        }
    }

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
