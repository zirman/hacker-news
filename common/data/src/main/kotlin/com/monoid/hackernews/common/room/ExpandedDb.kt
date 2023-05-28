package com.monoid.hackernews.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expanded")
data class ExpandedDb(
    @PrimaryKey
    @ColumnInfo(name = "itemId")
    val itemId: Long,
)
