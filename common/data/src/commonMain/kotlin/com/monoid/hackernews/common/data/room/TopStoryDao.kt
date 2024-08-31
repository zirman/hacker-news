package com.monoid.hackernews.common.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TopStoryDao {
    @Query("SELECT * FROM topstory ORDER BY `order`")
    fun getTopStories(): Flow<List<TopStoryDb>>

    @Query("DELETE FROM topstory")
    suspend fun deleteTopStories()

    @Upsert
    suspend fun upsertTopStories(topStories: List<TopStoryDb>)

    @Transaction
    suspend fun replaceTopStories(topStories: List<TopStoryDb>) {
        deleteTopStories()
        upsertTopStories(topStories)
    }
}
