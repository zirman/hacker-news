package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BestStoryDao {
    @Query("SELECT * FROM beststory ORDER BY `order`")
    fun getAll(): Flow<List<BestStory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceBestStories(bestStories: List<BestStory>)
}
