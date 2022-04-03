package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TopStoryDao {
    @Query("SELECT * FROM topstory ORDER BY `order`")
    fun getAll(): Flow<List<TopStory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceTopStories(topStories: List<TopStory>)
}
