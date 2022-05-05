package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getNewStories
import com.monoid.hackernews.room.NewStoryDb
import com.monoid.hackernews.room.NewStoryDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewStoryRepo(
    private val httpClient: HttpClient,
    private val newStoryDao: NewStoryDao,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
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

    override suspend fun updateRepoItems() {
        try {
            newStoryDao.replaceNewStories(
                httpClient.getNewStories().mapIndexed { order, storyId ->
                    NewStoryDb(
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
