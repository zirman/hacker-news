package com.monoid.hackernews.common.data.room

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "comment")
data class CommentDb(
    @PrimaryKey
    @ColumnInfo(name = "parentId")
    val parentId: Long,
    @ColumnInfo(name = "text")
    val text: String,
)
