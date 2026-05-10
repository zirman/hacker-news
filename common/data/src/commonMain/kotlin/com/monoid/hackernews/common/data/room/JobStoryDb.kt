package com.monoid.hackernews.common.data.room

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "job_story")
data class JobStoryDb(
    @ColumnInfo(name = "itemId")
    val itemId: Long,
    @ColumnInfo(name = "order")
    @PrimaryKey val order: Int,
)
