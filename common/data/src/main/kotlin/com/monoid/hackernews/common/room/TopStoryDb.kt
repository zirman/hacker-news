package com.monoid.hackernews.common.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "topstory")
@Serializable
data class TopStoryDb(
    val itemId: Long,
    @PrimaryKey val order: Int,
)
