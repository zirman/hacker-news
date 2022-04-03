package com.monoid.hackernews.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class User(
    @PrimaryKey val id: String,
    // last time item was retrieved from api
    val lastUpdate: Long,
    val created: Long,
    val karma: Int,
    val about: String?,
)

data class UserWithSubmitted(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "by",
    )
    val submitted: List<Item>
)
