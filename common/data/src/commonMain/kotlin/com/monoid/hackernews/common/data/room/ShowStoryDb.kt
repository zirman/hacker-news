package com.monoid.hackernews.common.data.room

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "show_story")
data class ShowStoryDb(
    @ColumnInfo(name = "itemId")
    val itemId: Long,
    @ColumnInfo(name = "order")
    @PrimaryKey val order: Int,
)
