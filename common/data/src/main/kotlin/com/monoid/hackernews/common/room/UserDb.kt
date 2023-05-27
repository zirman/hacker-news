package com.monoid.hackernews.common.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("UserDb")
@Entity(tableName = "user")
data class UserDb(
    @SerialName("id")
    @PrimaryKey val id: String,
    // last time item was retrieved from api
    @SerialName("lastUpdate")
    val lastUpdate: Long,
    @SerialName("created")
    val created: Long,
    @SerialName("karma")
    val karma: Int,
    @SerialName("about")
    val about: String?,
)
