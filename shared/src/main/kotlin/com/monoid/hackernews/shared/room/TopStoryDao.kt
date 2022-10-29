package com.monoid.hackernews.shared.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TopStoryDao {
    @Query("SELECT * FROM topstory ORDER BY `order`")
    fun getTopStories(): Flow<List<TopStoryDb>>

    @Query("DELETE FROM topstory")
    suspend fun deleteTopStories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopStories(topStories: List<TopStoryDb>)

    @Transaction
    suspend fun replaceTopStories(topStories: List<TopStoryDb>) {
        deleteTopStories()
        insertTopStories(topStories)
    }
}
