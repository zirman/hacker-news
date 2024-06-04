package com.monoid.hackernews.common.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun itemInsertStub(item: ItemDb)

    @Transaction
    suspend fun itemApiInsert(itemApi: ItemApi, instant: Instant) {
        // update children entries
        itemApi.kids.orEmpty().forEach { itemId ->
            itemInsertStub(ItemDb(id = itemId.long, parent = itemApi.id.long))
        }

        itemUpsert(itemApi.toItemDb(instant))
    }
}
