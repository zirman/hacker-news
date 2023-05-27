package com.monoid.hackernews.common.room

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("UpvoteDb")
@Entity(tableName = "upvote", primaryKeys = ["username", "itemId"])
data class UpvoteDb(
    @SerialName("username")
    val username: String,
    @SerialName("itemId")
    val itemId: Long,
)
