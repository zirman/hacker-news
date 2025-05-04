package com.monoid.hackernews.common.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_story")
data class JobStoryDb(
    @ColumnInfo(name = "itemId")
    val itemId: Long,
    @ColumnInfo(name = "order")
    @PrimaryKey val order: Int,
)
