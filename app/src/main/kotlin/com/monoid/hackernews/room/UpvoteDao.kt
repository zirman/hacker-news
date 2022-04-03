package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UpvoteDao {
    @Query("SELECT EXISTS (SELECT * FROM upvote WHERE itemId = :itemId AND username = :username)")
    fun isUpvote(itemId: Long, username: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(upvote: Upvote)

    @Delete
    suspend fun delete(upvote: Upvote)
}
