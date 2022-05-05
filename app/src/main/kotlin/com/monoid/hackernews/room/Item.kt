package com.monoid.hackernews.room

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Entity(tableName = "item")
@Serializable
data class ItemDb(
    @PrimaryKey val id: Long,
    // last time item was retrieved from api
    val lastUpdate: Long? = null,
    val type: String? = null,
    val time: Long? = null,
    val deleted: Boolean? = null,
    val by: String? = null,
    val descendants: Int? = null,
    val score: Int? = null,
    val title: String? = null,
    val text: String? = null,
    val url: String? = null,
    val parent: Long? = null,
)

@Immutable
data class ItemWithKids(
    @Embedded val item: ItemDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent",
    )
    val kids: List<ItemDb>,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId",
    )
    val favorites: List<FavoriteDb>,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId",
    )
    val upvotes: List<UpvoteDb>,
)

@Serializable
data class ItemTree(
    val item: ItemDb,
    val kids: List<ItemTree>?,
    val expanded: Boolean,
)

data class ItemUi(
    val item: ItemDb,
    val kidCount: Int,
    val expanded: Boolean,
    val depth: Int,
)
