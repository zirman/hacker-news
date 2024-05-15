package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FlagDao {
    @Query("SELECT * FROM flag WHERE username = :username")
    fun getFlagsForUser(username: String): Flow<List<FlagDb>>

    @Query("SELECT EXISTS (SELECT * FROM flag WHERE itemId = :itemId AND username = :username)")
    suspend fun isFlag(itemId: Long, username: String): Boolean

    @Query("SELECT EXISTS (SELECT * FROM flag WHERE itemId = :itemId AND username = :username)")
    fun isFlagFlow(itemId: Long, username: String): Flow<Boolean>

    @Insert
    suspend fun flagInsert(flag: FlagDb)

    @Delete
    suspend fun flagDelete(flag: FlagDb)

    @Query("DELETE FROM flag WHERE username = :username")
    suspend fun deleteFlagsForUser(username: String)

    @Insert
    suspend fun insertFlags(flags: List<FlagDb>)

    @Transaction
    suspend fun replaceFlagsForUser(username: String, flags: List<Long>) {
        deleteFlagsForUser(username)
        insertFlags(flags.map { FlagDb(username, it) })
    }
}
