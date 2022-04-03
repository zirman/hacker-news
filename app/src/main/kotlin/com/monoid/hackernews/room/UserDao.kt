package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :username")
    fun getUserByUsername(username: String): Flow<User>

    @Transaction
    @Query("SELECT * FROM user WHERE id = :username")
    fun userByUsernameWithSubmitted(username: String): Flow<UserWithSubmitted?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(user: User)
}
