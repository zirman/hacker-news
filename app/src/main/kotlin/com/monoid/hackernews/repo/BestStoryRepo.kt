package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getBestStories
import com.monoid.hackernews.room.BestStory
import com.monoid.hackernews.room.BestStoryDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BestStoryRepo(
    private val httpClient: HttpClient,
    private val bestStoryDao: BestStoryDao,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
        return bestStoryDao.getAll()
            .map { bestStories ->
                bestStories.map {
                    OrderedItem(
                        itemId = ItemId(it.itemId),
                        order = it.order,
                    )
                }
            }
    }

    override suspend fun updateRepoItems() {
        bestStoryDao.replaceBestStories(
            httpClient.getBestStories().mapIndexed { order, storyId ->
                BestStory(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
