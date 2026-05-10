package com.monoid.hackernews.common.data.room

import androidx.room3.Embedded
import androidx.room3.Relation

data class ItemWithKids(
    @Embedded val item: ItemDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent",
    )
    val kids: List<ItemDb>,
)
