package com.monoid.hackernews.room

import androidx.room.Entity

@Entity(primaryKeys = ["username", "itemId"])
data class Upvote(
    val username: String,
    val itemId: Long,
)
