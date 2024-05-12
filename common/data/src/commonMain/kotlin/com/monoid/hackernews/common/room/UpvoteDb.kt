package com.monoid.hackernews.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "upvote", primaryKeys = ["username", "itemId"])
data class UpvoteDb(
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "itemId")
    val itemId: Long,
)
