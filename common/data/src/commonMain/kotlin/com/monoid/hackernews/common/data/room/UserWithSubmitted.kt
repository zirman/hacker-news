package com.monoid.hackernews.common.data.room

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithSubmitted(
    @Embedded val user: UserDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "by",
    )
    val submitted: List<ItemDb>,
)
