package com.monoid.hackernews.shared.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "askstory")
@Serializable
data class AskStoryDb(
    val itemId: Long,
    @PrimaryKey val order: Int,
)
