package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getShowStories
import com.monoid.hackernews.room.ShowStoryDao
import com.monoid.hackernews.room.ShowStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShowStoryRepo(
    private val httpClient: HttpClient,
    private val showStoryDao: ShowStoryDao,
) : OrderedItemRepo() {
    override fun getDbItems(): Flow<List<OrderedItem>> {
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

    override suspend fun updateDbItems() {
        showStoryDao.replaceShowStories(
            httpClient.getShowStories().mapIndexed { order, storyId ->
                ShowStoryDb(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
