package com.monoid.hackernews.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.monoid.hackernews.common.data.ItemType

@Entity(tableName = "item")
data class ItemDb(
    @ColumnInfo(name = "id")
    @PrimaryKey val id: Long,
    // last time item was retrieved from api
    @ColumnInfo(name = "lastUpdate")
    val lastUpdate: Long? = null,
    @ColumnInfo(name = "type")
    val type: ItemType? = null,
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
    @ColumnInfo(name = "upvoted")
    val upvoted: Boolean? = null,
    @ColumnInfo(name = "favourited")
    val favourited: Boolean? = null,
    @ColumnInfo(name = "flagged")
    val flagged: Boolean? = null,
    // local only data
    @ColumnInfo(name = "expanded")
    val expanded: Boolean = true,
    @ColumnInfo(name = "followed")
    val followed: Boolean = false,
)
