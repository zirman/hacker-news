package com.monoid.hackernews.common.room

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation

@Immutable
data class UserWithSubmitted(
    @Embedded val user: UserDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "by",
    )
    val submitted: List<ItemDb>,
)
