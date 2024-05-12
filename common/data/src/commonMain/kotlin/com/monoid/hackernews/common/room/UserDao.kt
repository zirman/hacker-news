package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :username")
    fun getUserByUsername(username: String): Flow<UserDb>

    @Transaction
    @Query("SELECT * FROM user WHERE id = :username")
    fun userByUsernameWithSubmitted(username: String): Flow<UserWithSubmitted?>

    @Upsert
    suspend fun upsertReplace(user: UserDb)
}
