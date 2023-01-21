package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getAskStories
import com.monoid.hackernews.common.room.AskStoryDao
import com.monoid.hackernews.common.room.AskStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AskStoryRepository @Inject constructor(
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