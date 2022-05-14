package com.monoid.hackernews.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "item")
@Serializable
data class ItemDb(
    @PrimaryKey val id: Long,
    // last time item was retrieved from api
    val lastUpdate: Long? = null,
    val type: String? = null,
    val time: Long? = null,
    val deleted: Boolean? = null,
    val by: String? = null,
    val descendants: Int? = null,
    val score: Int? = null,
    val title: String? = null,
    val text: String? = null,
    val url: String? = null,
    val parent: Long? = null,
)
