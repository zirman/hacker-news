package com.monoid.hackernews.common.room

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ItemWithKids")
@Immutable
data class ItemWithKids(
    @SerialName("item")
    @Embedded val item: ItemDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent",
    )
    @SerialName("kids")
    val kids: List<ItemDb>,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId",
    )
    @SerialName("favorites")
    val favorites: List<FavoriteDb>,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId",
    )
    @SerialName("upvotes")
    val upvotes: List<UpvoteDb>,
)
