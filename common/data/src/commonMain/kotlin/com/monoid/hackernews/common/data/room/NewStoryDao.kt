package com.monoid.hackernews.common.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NewStoryDao {
    @Query("SELECT * FROM new_story ORDER BY `order`")
    fun getNewStories(): Flow<List<NewStoryDb>>

    @Query("DELETE FROM new_story")
    suspend fun deleteNewStories()

    @Upsert
    suspend fun upsertNewStories(newStories: List<NewStoryDb>)

    @Transaction
    suspend fun replaceNewStories(newStories: List<NewStoryDb>) {
        deleteNewStories()
        upsertNewStories(newStories)
    }
}
