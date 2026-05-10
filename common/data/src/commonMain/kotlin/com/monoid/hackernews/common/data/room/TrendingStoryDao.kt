package com.monoid.hackernews.common.data.room

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingStoryDao {
    @Query("SELECT * FROM trending_story ORDER BY `order`")
    fun getTrendingStories(): Flow<List<TrendingStoryDb>>

    @Query("DELETE FROM trending_story")
    suspend fun deleteTopStories()

    @Upsert
    suspend fun upsertTopStories(topStories: List<TrendingStoryDb>)

    @Transaction
    suspend fun replaceTopStories(topStories: List<TrendingStoryDb>) {
        deleteTopStories()
        upsertTopStories(topStories)
    }
}
