package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getTopStories
import com.monoid.hackernews.room.TopStoryDb
import com.monoid.hackernews.room.TopStoryDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TopStoryRepo(
    private val httpClient: HttpClient,
    private val topStoryDao: TopStoryDao,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
        return topStoryDao.getTopStories()
            .map { topStories ->
                topStories.map {
                    OrderedItem(
                        itemId = ItemId(it.itemId),
                        order = it.order,
                    )
                }
            }
    }

    override suspend fun updateRepoItems() {
        try {
            topStoryDao.replaceTopStories(
                httpClient.getTopStories().mapIndexed { order, storyId ->
                    TopStoryDb(
                        itemId = storyId,
                        order = order,
                    )
                }
            )
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
        }
    }
}
