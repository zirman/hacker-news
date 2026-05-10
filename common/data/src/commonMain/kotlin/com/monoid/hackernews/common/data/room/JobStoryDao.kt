package com.monoid.hackernews.common.data.room

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JobStoryDao {
    @Query("SELECT * FROM job_story ORDER BY `order`")
    fun getJobStories(): Flow<List<JobStoryDb>>

    @Query("DELETE FROM job_story")
    suspend fun deleteJobStories()

    @Upsert
    suspend fun upsertJobStories(jobStories: List<JobStoryDb>)

    @Transaction
    suspend fun replaceJobStories(topStories: List<JobStoryDb>) {
        deleteJobStories()
        upsertJobStories(topStories)
    }
}
