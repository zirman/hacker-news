package com.monoid.hackernews.shared.data

import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.api.getBestStories
import com.monoid.hackernews.shared.room.BestStoryDao
import com.monoid.hackernews.shared.room.BestStoryDb
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
