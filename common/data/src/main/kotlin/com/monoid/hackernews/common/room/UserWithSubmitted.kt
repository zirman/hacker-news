package com.monoid.hackernews.common.room

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("UserWithSubmitted")
@Immutable
data class UserWithSubmitted(
    @SerialName("user")
    @Embedded val user: UserDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "by",
    )
    @SerialName("submitted")
    val submitted: List<ItemDb>
)
