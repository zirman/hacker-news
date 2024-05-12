package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BestStoryDao {
    @Query("SELECT * FROM beststory ORDER BY `order`")
    fun getBestStories(): Flow<List<BestStoryDb>>

    @Query("DELETE FROM beststory")
    suspend fun deleteBestStories()

    @Upsert
    suspend fun upsertBestStories(bestStories: List<BestStoryDb>)

    @Transaction
    suspend fun replaceBestStories(bestStories: List<BestStoryDb>) {
        deleteBestStories()
        upsertBestStories(bestStories)
    }
}
