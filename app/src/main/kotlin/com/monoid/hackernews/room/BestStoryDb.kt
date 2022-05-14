package com.monoid.hackernews.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "beststory")
@Serializable
data class BestStoryDb(
    val itemId: Long,
    @PrimaryKey val order: Int,
)
