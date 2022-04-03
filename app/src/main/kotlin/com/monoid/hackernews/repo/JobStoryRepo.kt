package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getJobStories
import com.monoid.hackernews.room.JobStory
import com.monoid.hackernews.room.JobStoryDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JobStoryRepo(
    private val httpClient: HttpClient,
    private val jobStoryDao: JobStoryDao,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
        return jobStoryDao.getAll()
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
        jobStoryDao.replaceJobStories(
            httpClient.getJobStories().mapIndexed { order, storyId ->
                JobStory(
                    itemId = storyId,
                    order = order,
                )
            }
        )
    }
}
