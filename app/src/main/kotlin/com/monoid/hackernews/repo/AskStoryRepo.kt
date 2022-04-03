package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getAskStories
import com.monoid.hackernews.room.AskStory
import com.monoid.hackernews.room.AskStoryDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AskStoryRepo(
    private val httpClient: HttpClient,
    private val askStoryDao: AskStoryDao,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
        return askStoryDao.getAll()
            .map { askStories ->
                askStories.map {
                    OrderedItem(
                        itemId = ItemId(it.itemId),
                        order = it.order,
                    )
                }
            }
    }

    override suspend fun updateRepoItems() {
        askStoryDao.replaceAskStories(
            httpClient.getAskStories().mapIndexed { order, storyId ->
                AskStory(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
