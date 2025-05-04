package com.monoid.hackernews.common.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hot_story")
data class HotStoryDb(
    @ColumnInfo(name = "itemId")
    val itemId: Long,
    @ColumnInfo(name = "order")
    @PrimaryKey val order: Int,
)
