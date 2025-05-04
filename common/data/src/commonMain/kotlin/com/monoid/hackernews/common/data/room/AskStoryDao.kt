package com.monoid.hackernews.common.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AskStoryDao {
    @Query("SELECT * FROM ask_story ORDER BY `order`")
    fun getAskStories(): Flow<List<AskStoryDb>>

    @Query("DELETE FROM ask_story")
    suspend fun deleteAskStories()

    @Upsert
    suspend fun upsertAskStories(askStories: List<AskStoryDb>)

    @Transaction
    suspend fun replaceAskStories(askStories: List<AskStoryDb>) {
        deleteAskStories()
        upsertAskStories(askStories)
    }
}
