package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface JobStoryDao {
    @Query("SELECT * FROM jobstory ORDER BY `order`")
    fun getJobStories(): Flow<List<JobStoryDb>>

    @Query("DELETE FROM jobstory")
    suspend fun deleteJobStories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobStories(jobStories: List<JobStoryDb>)

    @Transaction
    suspend fun replaceJobStories(topStories: List<JobStoryDb>) {
        deleteJobStories()
        insertJobStories(topStories)
    }
}
