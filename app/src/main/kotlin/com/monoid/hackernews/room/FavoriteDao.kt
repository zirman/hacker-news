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
    fun getFavoritesForUser(username: String): Flow<List<Favorite>>

    @Query("SELECT EXISTS (SELECT * FROM favorite WHERE itemId = :itemId AND username = :username)")
    fun isFavorite(itemId: Long, username: String): Flow<Boolean>

    @Insert
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE username = :username")
    suspend fun deleteFavoritesForUser(username: String)

    @Insert
    suspend fun insertFavorites(favorites: List<Favorite>)

    @Transaction
    suspend fun replaceFavoritesForUser(username: String, favorites: List<ItemId>) {
        deleteFavoritesForUser(username)
        insertFavorites(favorites.map { Favorite(username, it.long) })
    }
}
