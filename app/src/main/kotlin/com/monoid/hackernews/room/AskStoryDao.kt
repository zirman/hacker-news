package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AskStoryDao {
    @Query("SELECT * FROM askstory ORDER BY `order`")
    fun getAskStories(): Flow<List<AskStory>>

    @Query("DELETE FROM askstory")
    suspend fun deleteAskStories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAskStories(askStories: List<AskStory>)

    @Transaction
    suspend fun replaceAskStories(askStories: List<AskStory>) {
        deleteAskStories()
        insertAskStories(askStories)
    }
}
