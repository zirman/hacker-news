package com.monoid.hackernews.common.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("TopStoryDb")
@Entity(tableName = "topstory")
data class TopStoryDb(
    @SerialName("itemId")
    val itemId: Long,
    @SerialName("order")
    @PrimaryKey val order: Int,
)
