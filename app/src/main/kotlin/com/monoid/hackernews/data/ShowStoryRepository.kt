package com.monoid.hackernews.data

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getShowStories
import com.monoid.hackernews.room.ShowStoryDao
import com.monoid.hackernews.room.ShowStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShowStoryRepository(
    private val httpClient: HttpClient,
    private val showStoryDao: ShowStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
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

    override suspend fun updateItems() {
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
