package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NewStoryDao {
    @Query("SELECT * FROM newstory ORDER BY `order`")
    fun getNewStories(): Flow<List<NewStoryDb>>

    @Query("DELETE FROM newstory")
    suspend fun deleteNewStories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewStories(newStories: List<NewStoryDb>)

    @Transaction
    suspend fun replaceNewStories(newStories: List<NewStoryDb>) {
        deleteNewStories()
        insertNewStories(newStories)
    }
}
