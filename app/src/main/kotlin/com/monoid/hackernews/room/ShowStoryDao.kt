package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowStoryDao {
    @Query("SELECT * FROM showstory ORDER BY `order`")
    fun getAll(): Flow<List<ShowStory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceShowStories(topStories: List<ShowStory>)
}
