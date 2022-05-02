package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getShowStories
import com.monoid.hackernews.room.ShowStory
import com.monoid.hackernews.room.ShowStoryDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShowStoryRepo(
    private val httpClient: HttpClient,
    private val showStoryDao: ShowStoryDao,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
        return showStoryDao.getShowStories()
            .map { showStories ->
                showStories.map {
                    OrderedItem(
                        itemId = ItemId(it.itemId),
                        order = it.order,
                    )
                }
            }
    }

    override suspend fun updateRepoItems() {
        try {
            showStoryDao.replaceShowStories(
                httpClient.getShowStories().mapIndexed { order, storyId ->
                    ShowStory(
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
