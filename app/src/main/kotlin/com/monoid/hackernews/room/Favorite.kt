package com.monoid.hackernews.room

import androidx.room.Entity

@Entity(primaryKeys = ["username", "itemId"])
data class Favorite(
    val username: String,
    val itemId: Long,
)
