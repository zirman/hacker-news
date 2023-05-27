package com.monoid.hackernews.common.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ItemDb")
@Entity(tableName = "item")
data class ItemDb(
    @SerialName("id")
    @PrimaryKey val id: Long,
    // last time item was retrieved from api
    @SerialName("lastUpdate")
    val lastUpdate: Long? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("time")
    val time: Long? = null,
    @SerialName("deleted")
    val deleted: Boolean? = null,
    @SerialName("by")
    val by: String? = null,
    @SerialName("descendants")
    val descendants: Int? = null,
    @SerialName("score")
    val score: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("parent")
    val parent: Long? = null,
)
