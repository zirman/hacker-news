package com.monoid.hackernews.common.room

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FlagDb")
@Entity(tableName = "flag", primaryKeys = ["username", "itemId"])
data class FlagDb(
    @SerialName("username")
    val username: String,
    @SerialName("itemId")
    val itemId: Long,
)
