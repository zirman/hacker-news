package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.monoid.hackernews.api.ItemId
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite WHERE username = :username")
    fun getFavoritesForUser(username: String): Flow<List<FavoriteDb>>

    @Query("SELECT EXISTS (SELECT * FROM favorite WHERE itemId = :itemId AND username = :username)")
    fun isFavorite(itemId: Long, username: String): Boolean

    @Query("SELECT EXISTS (SELECT * FROM favorite WHERE itemId = :itemId AND username = :username)")
    fun isFavoriteFlow(itemId: Long, username: String): Flow<Boolean>

    @Insert
    suspend fun favoriteInsert(favorite: FavoriteDb)

    @Delete
    suspend fun favoriteDelete(favorite: FavoriteDb)

    @Query("DELETE FROM favorite WHERE username = :username")
    suspend fun deleteFavoritesForUser(username: String)

    @Insert
    suspend fun insertFavorites(favorites: List<FavoriteDb>)

    @Transaction
    suspend fun replaceFavoritesForUser(username: String, favorites: List<ItemId>) {
        deleteFavoritesForUser(username)
        insertFavorites(favorites.map { FavoriteDb(username, it.long) })
    }
}
