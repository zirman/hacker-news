package com.monoid.hackernews.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class ItemDb(
    @ColumnInfo(name = "id")
    @PrimaryKey val id: Long,
    // last time item was retrieved from api
    @ColumnInfo(name = "lastUpdate")
    val lastUpdate: Long? = null,
    @ColumnInfo(name = "type")
    val type: String? = null,
    @ColumnInfo(name = "time")
    val time: Long? = null,
    @ColumnInfo(name = "deleted")
    val deleted: Boolean? = null,
    @ColumnInfo(name = "by")
    val by: String? = null,
    @ColumnInfo(name = "descendants")
    val descendants: Int? = null,
    @ColumnInfo(name = "score")
    val score: Int? = null,
    @ColumnInfo(name = "title")
    val title: String? = null,
    @ColumnInfo(name = "text")
    val text: String? = null,
    @ColumnInfo(name = "url")
    val url: String? = null,
    @ColumnInfo(name = "parent")
    val parent: Long? = null,
)
