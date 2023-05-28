package com.monoid.hackernews.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserDb(
    @ColumnInfo(name = "id")
    @PrimaryKey val id: String,
    // last time item was retrieved from api
    @ColumnInfo(name = "lastUpdate")
    val lastUpdate: Long,
    @ColumnInfo(name = "created")
    val created: Long,
    @ColumnInfo(name = "karma")
    val karma: Int,
    @ColumnInfo(name = "about")
    val about: String?,
)
