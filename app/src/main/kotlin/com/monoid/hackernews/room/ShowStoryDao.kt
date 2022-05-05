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
    fun getShowStories(): Flow<List<ShowStoryDb>>

    @Query("DELETE FROM showstory")
    suspend fun deleteShowStories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShowStories(showStories: List<ShowStoryDb>)

    @Transaction
    suspend fun replaceShowStories(showStories: List<ShowStoryDb>) {
        deleteShowStories()
        insertShowStories(showStories)
    }
}
