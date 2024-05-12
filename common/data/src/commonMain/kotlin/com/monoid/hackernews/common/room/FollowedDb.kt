package com.monoid.hackernews.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "followed")
data class FollowedDb(
    @PrimaryKey
    @ColumnInfo(name = "itemId")
    val itemId: Long,
)
