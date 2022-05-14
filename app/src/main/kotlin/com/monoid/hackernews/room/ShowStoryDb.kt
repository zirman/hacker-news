package com.monoid.hackernews.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "showstory")
@Serializable
data class ShowStoryDb(
    val itemId: Long,
    @PrimaryKey val order: Int,
)
