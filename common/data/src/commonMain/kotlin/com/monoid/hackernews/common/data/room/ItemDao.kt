package com.monoid.hackernews.common.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.monoid.hackernews.common.data.api.ItemApi
import com.monoid.hackernews.common.data.api.toItemDb
import com.monoid.hackernews.common.data.model.Item
import kotlinx.datetime.Instant

@Dao
interface ItemDao {
    @Query("SELECT * FROM item WHERE id = :itemId")
    suspend fun itemById(itemId: Long): ItemDb?

    @Transaction
    @Query("SELECT * FROM item WHERE id = :itemId")
    suspend fun itemByIdWithKidsByIdInternal(itemId: Long): ItemWithKids?

    @Transaction
    suspend fun itemByIdWithKidsById(itemId: Long): ItemWithKids? =
        itemByIdWithKidsByIdInternal(itemId)
            ?.let { item -> item.copy(kids = item.kids.sortedBy { it.id }) }

    @Upsert(entity = ItemDb::class)
    suspend fun itemUpsert(item: ItemDb)

    @Query("UPDATE item SET upvoted = :upvoted WHERE id = :itemId")
    suspend fun setUpvotedByItemId(itemId: Long, upvoted: Boolean): Int

    @Query("UPDATE item SET favorited = :favorited WHERE id = :itemId")
    suspend fun setFavoritedByItemId(itemId: Long, favorited: Boolean): Int

    @Query("UPDATE item SET followed = :followed WHERE id = :itemId")
    suspend fun setFollowedByItemId(itemId: Long, followed: Boolean): Int

    @Transaction
    suspend fun itemToggleExpanded(itemId: Long): ItemWithKids? =
        itemByIdWithKidsById(itemId)
            ?.let { item -> item.copy(item = item.item.copy(expanded = item.item.expanded.not())) }
            ?.also { itemUpsert(it.item) }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun itemInsertStub(item: ItemDb)

    @Transaction
    suspend fun itemApiInsert(
        instant: Instant,
        itemApi: ItemApi,
        item: Item?,
    ) {
        // update children entries
        itemApi.kids?.forEach { itemId ->
            // TODO: multi insert
            itemInsertStub(ItemDb(id = itemId.long, parent = itemApi.id.long))
        }
        itemUpsert(
            itemApi.toItemDb(
                instant = instant,
                expanded = item?.expanded ?: EXPANDED_DEFAULT,
                followed = item?.followed ?: FOLLOWED_DEFAULT,
            )
        )
    }
}

internal const val EXPANDED_DEFAULT = true
internal const val FOLLOWED_DEFAULT = false
