package com.monoid.hackernews.common.data.room

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

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
