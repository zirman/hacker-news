package com.monoid.hackernews.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expanded")
data class ExpandedDb(
    @PrimaryKey
    val itemId: Long,
)
