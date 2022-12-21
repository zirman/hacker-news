package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BestStoryDao {
    @Query("SELECT * FROM beststory ORDER BY `order`")
    fun getBestStories(): Flow<List<BestStoryDb>>

    @Query("DELETE FROM beststory")
    suspend fun deleteBestStories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBestStories(bestStories: List<BestStoryDb>)

    @Transaction
    suspend fun replaceBestStories(bestStories: List<BestStoryDb>) {
        deleteBestStories()
        insertBestStories(bestStories)
    }
}
