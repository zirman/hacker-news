package com.monoid.hackernews.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class ShowStory(
    val itemId: Long,
    @PrimaryKey val order: Int,
)
