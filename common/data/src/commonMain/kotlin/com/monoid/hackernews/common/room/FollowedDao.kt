package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowedDao {
    @Query("SELECT EXISTS (SELECT * FROM followed WHERE itemId = :itemId)")
    fun isFollowed(itemId: Long): Boolean

    @Query("SELECT EXISTS (SELECT * FROM followed WHERE itemId = :itemId)")
    fun isFollowedFlow(itemId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun followedInsert(followed: FollowedDb)

    @Delete
    suspend fun followedDelete(followed: FollowedDb)
}
