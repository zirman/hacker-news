package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JobStoryDao {
    @Query("SELECT * FROM jobstory ORDER BY `order`")
    fun getAll(): Flow<List<JobStory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceJobStories(topStories: List<JobStory>)
}
