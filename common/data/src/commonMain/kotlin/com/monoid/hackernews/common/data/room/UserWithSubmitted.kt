package com.monoid.hackernews.common.data.room

import androidx.room3.Embedded
import androidx.room3.Relation

data class UserWithSubmitted(
    @Embedded val user: UserDb,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["by"],
    )
    val submitted: List<ItemDb>,
)
