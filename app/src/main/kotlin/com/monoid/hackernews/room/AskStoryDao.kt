package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AskStoryDao {
    @Query("SELECT * FROM askstory ORDER BY `order`")
    fun getAll(): Flow<List<AskStory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceAskStories(bestStories: List<AskStory>)
}
