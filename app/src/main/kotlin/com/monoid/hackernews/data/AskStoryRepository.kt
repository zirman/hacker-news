package com.monoid.hackernews.data

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getAskStories
import com.monoid.hackernews.room.AskStoryDao
import com.monoid.hackernews.room.AskStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AskStoryRepository(
    private val httpClient: HttpClient,
    private val askStoryDao: AskStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
        return askStoryDao.getAskStories()
            .map { askStories ->
                askStories.map {
                    OrderedItem(
                        itemId = ItemId(it.itemId),
                        order = it.order,
                    )
                }
            }
    }

    override suspend fun updateItems() {
        askStoryDao.replaceAskStories(
            httpClient.getAskStories().mapIndexed { order, storyId ->
                AskStoryDb(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
