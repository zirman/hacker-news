package com.monoid.hackernews.common.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowStoryDao {
    @Query("SELECT * FROM show_story ORDER BY `order`")
    fun getShowStories(): Flow<List<ShowStoryDb>>

    @Query("DELETE FROM show_story")
    suspend fun deleteShowStories()

    @Upsert
    suspend fun upsertShowStories(showStories: List<ShowStoryDb>)

    @Transaction
    suspend fun replaceShowStories(showStories: List<ShowStoryDb>) {
        deleteShowStories()
        upsertShowStories(showStories)
    }
}
