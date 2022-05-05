package com.monoid.hackernews.room

import androidx.room.Entity

@Entity(tableName = "favorite", primaryKeys = ["username", "itemId"])
data class FavoriteDb(
    val username: String,
    val itemId: Long,
)
