package com.monoid.hackernews.common.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "newstory")
@Serializable
data class NewStoryDb(
    val itemId: Long,
    @PrimaryKey val order: Int,
)
