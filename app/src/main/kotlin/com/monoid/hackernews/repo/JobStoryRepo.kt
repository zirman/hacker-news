package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getJobStories
import com.monoid.hackernews.room.JobStoryDb
import com.monoid.hackernews.room.JobStoryDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JobStoryRepo(
    private val httpClient: HttpClient,
    private val jobStoryDao: JobStoryDao,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
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

    override suspend fun updateRepoItems() {
        try {
            jobStoryDao.replaceJobStories(
                httpClient.getJobStories().mapIndexed { order, storyId ->
                    JobStoryDb(
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
