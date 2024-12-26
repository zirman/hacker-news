package com.monoid.hackernews.common.data.model

import androidx.compose.ui.text.AnnotatedString
import com.monoid.hackernews.common.data.api.ItemId

actual fun makeItem(
    id: ItemId,
    lastUpdate: Long?,
    type: ItemType?,
    time: Long?,
    deleted: Boolean?,
    by: String?,
    descendants: Int?,
    score: Int?,
    title: AnnotatedString?,
    text: AnnotatedString?,
    url: String?,
    parent: ItemId?,
    kids: List<ItemId>?,
    upvoted: Boolean?,
    favourited: Boolean?,
    flagged: Boolean?,
    expanded: Boolean,
    followed: Boolean
): Item {
    TODO("Not yet implemented")
}
