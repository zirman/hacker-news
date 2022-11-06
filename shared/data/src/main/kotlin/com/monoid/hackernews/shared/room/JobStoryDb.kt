package com.monoid.hackernews.shared.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "jobstory")
@Serializable
data class JobStoryDb(
    val itemId: Long,
    @PrimaryKey val order: Int,
)
