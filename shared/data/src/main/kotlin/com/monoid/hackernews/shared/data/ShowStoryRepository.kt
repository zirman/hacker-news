package com.monoid.hackernews.shared.data

import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.api.getShowStories
import com.monoid.hackernews.shared.room.ShowStoryDao
import com.monoid.hackernews.shared.room.ShowStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShowStoryRepository @Inject constructor(
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
