package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getJobStories
import com.monoid.hackernews.common.room.JobStoryDao
import com.monoid.hackernews.common.room.JobStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobStoryRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val jobStoryDao: JobStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = jobStoryDao
        .getJobStories().map { jobStories ->
            jobStories.map {
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
        jobStoryDao.replaceJobStories(
            httpClient.getJobStories().mapIndexed { order, storyId ->
                JobStoryDb(
                    itemId = storyId,
                    order = order
                )
            }
        )
    }
}
