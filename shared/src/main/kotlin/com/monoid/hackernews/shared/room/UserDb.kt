package com.monoid.hackernews.shared.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserDb(
    @PrimaryKey val id: String,
    // last time item was retrieved from api
    val lastUpdate: Long,
    val created: Long,
    val karma: Int,
    val about: String?,
)
