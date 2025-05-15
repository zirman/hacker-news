package com.monoid.hackernews.common.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comment")
data class CommentDb(
    @PrimaryKey
    @ColumnInfo(name = "parentId")
    val parentId: Long,
    @ColumnInfo(name = "text")
    val text: String,
)
