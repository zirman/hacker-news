package com.monoid.hackernews.common.room

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation

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
