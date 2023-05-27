package com.monoid.hackernews.common.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ExpandedDb")
@Entity(tableName = "expanded")
data class ExpandedDb(
    @PrimaryKey
    @SerialName("itemId")
    val itemId: Long,
)
