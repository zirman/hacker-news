package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JobStoryDao {
    @Query("SELECT * FROM jobstory ORDER BY `order`")
    fun getJobStories(): Flow<List<JobStoryDb>>

    @Query("DELETE FROM jobstory")
    suspend fun deleteJobStories()

    @Upsert
    suspend fun upsertJobStories(jobStories: List<JobStoryDb>)

    @Transaction
    suspend fun replaceJobStories(topStories: List<JobStoryDb>) {
        deleteJobStories()
        upsertJobStories(topStories)
    }
}
