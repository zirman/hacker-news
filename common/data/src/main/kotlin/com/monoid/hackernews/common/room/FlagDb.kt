package com.monoid.hackernews.common.room

import androidx.room.Entity

@Entity(tableName = "flag", primaryKeys = ["username", "itemId"])
data class FlagDb(
    val username: String,
    val itemId: Long,
)
