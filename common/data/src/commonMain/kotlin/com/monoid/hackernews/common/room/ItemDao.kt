package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.monoid.hackernews.common.api.ItemApi
import com.monoid.hackernews.common.api.toItemDb
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item WHERE id = :itemId")
    suspend fun itemById(itemId: Long): ItemDb?

    @Query("SELECT * FROM item WHERE id = :itemId")
    fun itemByIdFlow(itemId: Long): Flow<ItemDb?>

    @Transaction
    @Query("SELECT * FROM item WHERE id = :itemId")
    suspend fun itemByIdWithKidsById(itemId: Long): ItemWithKids?

    @Transaction
    @Query("SELECT * FROM item WHERE id = :itemId")
    fun itemByIdWithKidsByIdFlow(itemId: Long): Flow<ItemWithKids?>

    @Upsert(entity = ItemDb::class)
    suspend fun itemUpsert(item: ItemDb)

    @Insert(entity = ItemDb::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun itemsInsert(items: List<ItemDb>)

//    @Transaction
//    suspend fun itemApiInsert(itemApi: ItemApi) {
//        // update children entries
//        if (itemApi.kids != null) {
//            itemApi.kids.forEach { itemId ->
//                itemUpsert(
//                    (itemById(itemId.long) ?: ItemDb(id = itemId.long))
//                        .copy(parent = itemApi.id.long)
//                )
//            }
//        }
//
//        itemUpsert(itemApi.toItemDb())
//    }
}
