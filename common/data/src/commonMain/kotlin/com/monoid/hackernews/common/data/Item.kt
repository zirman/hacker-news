package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId

interface Item {
    val id: ItemId
    val lastUpdate: Long?
    val type: String?
    val time: Long?
    val deleted: Boolean?
    val by: String?
    val descendants: Int?
    val score: Int?
    val title: String?
    val text: String?
    val url: String?
    val parent: ItemId?
    val kids: List<ItemId>?
    val upvoted: Boolean?
    val favourited: Boolean?
    val flagged: Boolean?
    // local only data
    val expanded: Boolean
    val followed: Boolean
}

expect fun makeItem(
    id: ItemId,
    lastUpdate: Long? = null,
    type: String? = null,
    time: Long? = null,
    deleted: Boolean? = null,
    by: String? = null,
    descendants: Int? = null,
    score: Int? = null,
    title: String? = null,
    text: String? = null,
    url: String? = null,
    parent: ItemId? = null,
    kids: List<ItemId>? = null,
    upvoted: Boolean? = null,
    favourited: Boolean? = null,
    flagged: Boolean? = null,
    expanded: Boolean = true,
    followed: Boolean = false,
): Item
