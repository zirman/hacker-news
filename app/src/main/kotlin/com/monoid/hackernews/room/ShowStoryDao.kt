package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowStoryDao {
    @Query("SELECT * FROM showstory ORDER BY `order`")
    fun getShowStories(): Flow<List<ShowStory>>

    @Query("DELETE FROM showstory")
    suspend fun deleteShowStories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShowStories(showStories: List<ShowStory>)

    @Transaction
    suspend fun replaceShowStories(showStories: List<ShowStory>) {
        deleteShowStories()
        insertShowStories(showStories)
    }
}
