package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getBestStories
import com.monoid.hackernews.room.BestStoryDao
import com.monoid.hackernews.room.BestStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BestStoryRepo(
    private val httpClient: HttpClient,
    private val bestStoryDao: BestStoryDao,
) : OrderedItemRepo() {
    override fun getDbItems(): Flow<List<OrderedItem>> {
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

    override suspend fun updateDbItems() {
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
