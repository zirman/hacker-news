package com.monoid.hackernews.shared.data

import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.api.getJobStories
import com.monoid.hackernews.shared.room.JobStoryDao
import com.monoid.hackernews.shared.room.JobStoryDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JobStoryRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val jobStoryDao: JobStoryDao,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
        return jobStoryDao.getJobStories()
            .map { jobStories ->
                jobStories.map {
                    OrderedItem(
                        itemId = ItemId(it.itemId),
                        order = it.order,
                    )
                }
            }
    }

    override suspend fun updateItems() {
        jobStoryDao.replaceJobStories(
            httpClient.getJobStories().mapIndexed { order, storyId ->
                JobStoryDb(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
