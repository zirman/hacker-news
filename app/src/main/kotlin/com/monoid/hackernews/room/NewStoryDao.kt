package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewStoryDao {
    @Query("SELECT * FROM newstory ORDER BY `order`")
    fun getAll(): Flow<List<NewStory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceNewStories(topStories: List<NewStory>)
}
