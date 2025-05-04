package com.monoid.hackernews.common.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HotStoryDao {
    @Query("SELECT * FROM hot_story ORDER BY `order`")
    fun getHotStories(): Flow<List<HotStoryDb>>

    @Query("DELETE FROM hot_story")
    suspend fun deleteBestStories()

    @Upsert
    suspend fun upsertBestStories(bestStories: List<HotStoryDb>)

    @Transaction
    suspend fun replaceHotStories(bestStories: List<HotStoryDb>) {
        deleteBestStories()
        upsertBestStories(bestStories)
    }
}
