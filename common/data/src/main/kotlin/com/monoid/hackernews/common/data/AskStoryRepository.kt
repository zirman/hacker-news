package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getAskStories
import com.monoid.hackernews.common.room.AskStoryDao
import com.monoid.hackernews.common.room.AskStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AskStoryRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val askStoryDao: AskStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = askStoryDao
        .getAskStories()
        .map { askStories ->
            askStories.map {
                OrderedItem(
                    itemId = ItemId(it.itemId),
                    order = it.order
                )
            }
        }
        .shareIn(
            scope = scope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    override suspend fun updateItems() {
        askStoryDao.replaceAskStories(
            httpClient.getAskStories().mapIndexed { order, storyId ->
                AskStoryDb(
                    itemId = storyId,
                    order = order
                )
            }
        )
    }
}
