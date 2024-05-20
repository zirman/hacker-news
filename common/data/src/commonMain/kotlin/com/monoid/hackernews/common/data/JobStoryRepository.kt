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

class JobStoryRepository(
    private val remoteDataSource: HttpClient,
    private val jobStoryLocalDataSource: JobStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = jobStoryLocalDataSource
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
        jobStoryLocalDataSource.replaceJobStories(
            remoteDataSource.getJobStories().mapIndexed { order, storyId ->
                JobStoryDb(
                    itemId = storyId,
                    order = order
                )
            }
        )
    }
}
