package com.monoid.hackernews.common.data.room

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
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
