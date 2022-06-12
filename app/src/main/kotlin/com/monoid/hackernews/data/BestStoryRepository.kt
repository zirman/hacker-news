package com.monoid.hackernews.data

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getBestStories
import com.monoid.hackernews.room.BestStoryDao
import com.monoid.hackernews.room.BestStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BestStoryRepository(
    private val httpClient: HttpClient,
    private val bestStoryDao: BestStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
        return bestStoryDao.getBestStories()
            .map { bestStories ->
                bestStories.map {
                    OrderedItem(
                        itemId = ItemId(it.itemId),
                        order = it.order,
                    )
                }
            }
    }

    override suspend fun updateItems() {
        bestStoryDao.replaceBestStories(
            httpClient.getBestStories().mapIndexed { order, storyId ->
                BestStoryDb(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
