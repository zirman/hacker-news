package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.monoid.hackernews.api.ItemId
import kotlinx.coroutines.flow.Flow

@Dao
interface UpvoteDao {
    @Query("SELECT EXISTS (SELECT * FROM upvote WHERE itemId = :itemId AND username = :username)")
    fun isUpvote(itemId: Long, username: String): Flow<Boolean>

    @Insert
    suspend fun insertUpvote(upvote: Upvote)

    @Delete
    suspend fun deleteUpvote(upvote: Upvote)

    @Query("DELETE FROM upvote WHERE username = :username")
    suspend fun deleteUpvotesForUser(username: String)

    @Insert
    suspend fun insertUpvotes(favorites: List<Upvote>)

    @Transaction
    suspend fun replaceUpvotesForUser(username: String, upvotes: List<ItemId>) {
        deleteUpvotesForUser(username)
        insertUpvotes(upvotes.map { Upvote(username, it.long) })
    }
}
