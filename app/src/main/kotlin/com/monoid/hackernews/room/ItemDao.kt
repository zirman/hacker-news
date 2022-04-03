package com.monoid.hackernews.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item WHERE id = :itemId")
    fun itemByIdFlow(itemId: Long): Flow<Item?>

    @Transaction
    @Query("SELECT * FROM item WHERE id = :itemId")
    suspend fun itemByIdWithKidsById(itemId: Long): ItemWithKids?

    @Transaction
    @Query("SELECT * FROM item WHERE id = :itemId")
    fun itemByIdWithKidsByIdFlow(itemId: Long): Flow<ItemWithKids?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIdsIgnore(items: List<Item>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(items: Item)
}
