package com.monoid.hackernews.shared.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpandedDao {
    @Query("SELECT EXISTS (SELECT * FROM expanded WHERE itemId = :itemId)")
    fun isExpanded(itemId: Long): Boolean

    @Query("SELECT EXISTS (SELECT * FROM expanded WHERE itemId = :itemId)")
    fun isExpandedFlow(itemId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun expandedInsert(expanded: ExpandedDb)

    @Delete
    suspend fun expandedDelete(expanded: ExpandedDb)
}
