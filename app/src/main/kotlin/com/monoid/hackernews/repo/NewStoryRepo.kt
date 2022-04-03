package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getTopStories
import com.monoid.hackernews.room.NewStory
import com.monoid.hackernews.room.NewStoryDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewStoryRepo(
    private val httpClient: HttpClient,
    private val newStoryDao: NewStoryDao,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
        return newStoryDao.getAll()
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
        newStoryDao.replaceNewStories(
            httpClient.getTopStories().mapIndexed { order, storyId ->
                NewStory(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
