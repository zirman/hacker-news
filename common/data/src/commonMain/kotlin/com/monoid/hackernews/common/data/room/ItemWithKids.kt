package com.monoid.hackernews.common.data.room

import androidx.room.Embedded
import androidx.room.Relation

data class ItemWithKids(
    @Embedded val item: ItemDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent",
    )
    val kids: List<ItemDb>,
)
