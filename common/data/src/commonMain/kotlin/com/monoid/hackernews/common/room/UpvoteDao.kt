package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UpvoteDao {
    @Query("SELECT EXISTS (SELECT * FROM upvote WHERE itemId = :itemId AND username = :username)")
    suspend fun isUpvote(itemId: Long, username: String): Boolean

    @Query("SELECT EXISTS (SELECT * FROM upvote WHERE itemId = :itemId AND username = :username)")
    fun isUpvoteFlow(itemId: Long, username: String): Flow<Boolean>

    @Insert
    suspend fun upvoteInsert(upvote: UpvoteDb)

    @Delete
    suspend fun upvoteDelete(upvote: UpvoteDb)

    @Query("DELETE FROM upvote WHERE username = :username")
    suspend fun deleteUpvotesForUser(username: String)

    @Insert
    suspend fun insertUpvotes(favorites: List<UpvoteDb>)

    @Transaction
    suspend fun replaceUpvotesForUser(username: String, upvotes: List<Long>) {
        deleteUpvotesForUser(username)
        insertUpvotes(upvotes.map { UpvoteDb(username, it) })
    }
}
