package com.monoid.hackernews.common.data.model

import androidx.compose.ui.text.AnnotatedString
import com.monoid.hackernews.common.data.api.ItemId

data class Item(
    val id: ItemId,
    val lastUpdate: Long? = null,
    val type: ItemType? = null,
    val time: Long? = null,
    val deleted: Boolean? = null,
    val by: Username? = null,
    val descendants: Int? = null,
    val score: Int? = null,
    val title: String? = null,
    val text: AnnotatedString? = null,
    val url: String? = null,
    val parent: ItemId? = null,
    val kids: List<ItemId>? = null,
    val upvoted: Boolean? = null,
    val favorited: Boolean? = null,
    val flagged: Boolean? = null,

    // local only data

    val expanded: Boolean = true,
    val followed: Boolean = false,
)
