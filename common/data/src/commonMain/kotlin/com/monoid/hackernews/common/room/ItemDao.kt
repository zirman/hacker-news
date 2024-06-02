package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.monoid.hackernews.common.api.ItemApi
import com.monoid.hackernews.common.api.toItemDb
import kotlinx.datetime.Instant

@Dao
interface ItemDao {
    @Query("SELECT * FROM item WHERE id = :itemId")
    suspend fun itemById(itemId: Long): ItemDb?

    @Transaction
    @Query("SELECT * FROM item WHERE id = :itemId")
    suspend fun itemByIdWithKidsById(itemId: Long): ItemWithKids?

    @Upsert(entity = ItemDb::class)
    suspend fun itemUpsert(item: ItemDb)

    @Transaction
    suspend fun itemApiInsert(itemApi: ItemApi, instant: Instant) {
        // update children entries
        if (itemApi.kids != null) {
            itemApi.kids.forEach { itemId ->
                itemUpsert(
                    (itemById(itemId.long) ?: ItemDb(id = itemId.long))
                        .copy(parent = itemApi.id.long)
                )
            }
        }

        itemUpsert(itemApi.toItemDb(instant))
    }
}
