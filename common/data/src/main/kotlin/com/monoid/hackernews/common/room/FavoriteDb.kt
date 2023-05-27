package com.monoid.hackernews.common.room

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FavoriteDb")
@Entity(tableName = "favorite", primaryKeys = ["username", "itemId"])
data class FavoriteDb(
    @SerialName("username")
    val username: String,
    @SerialName("itemId")
    val itemId: Long,
)
