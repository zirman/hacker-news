package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite WHERE username = :username")
    fun getAll(username: String): Flow<List<Favorite>>

    @Query("SELECT EXISTS (SELECT * FROM favorite WHERE itemId = :itemId AND username = :username)")
    fun isFavorite(itemId: Long, username: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)
}
