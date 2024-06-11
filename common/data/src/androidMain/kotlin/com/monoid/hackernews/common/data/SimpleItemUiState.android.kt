package com.monoid.hackernews.common.data

import android.os.Parcelable
import com.monoid.hackernews.common.api.ItemId
import kotlinx.parcelize.Parcelize

@Parcelize
data class SimpleItemUiStateImpl(
    override val id: ItemId,
    override val lastUpdate: Long?,
    override val type: String?,
    override val time: Long?,
    override val deleted: Boolean?,
    override val by: String?,
    override val descendants: Int?,
    override val score: Int?,
    override val title: String?,
    override val text: String?,
    override val url: String?,
    override val parent: ItemId?,
    override val kids: List<ItemId>?,
    override val upvoted: Boolean?,
    override val favourited: Boolean?,
    override val flagged: Boolean?,
    override val expanded: Boolean,
    override val followed: Boolean,
) : Item, Parcelable

actual fun makeItem(
    id: ItemId,
    lastUpdate: Long?,
    type: String?,
    time: Long?,
    deleted: Boolean?,
    by: String?,
    descendants: Int?,
    score: Int?,
    title: String?,
    text: String?,
    url: String?,
    parent: ItemId?,
    kids: List<ItemId>?,
    upvoted: Boolean?,
    favourited: Boolean?,
    flagged: Boolean?,
    expanded: Boolean,
    followed: Boolean,
): Item = SimpleItemUiStateImpl(
    id = id,
    lastUpdate = lastUpdate,
    type = type,
    time = time,
    deleted = deleted,
    by = by,
    descendants = descendants,
    score = score,
    title = title,
    text = text,
    url = url,
    parent = parent,
    kids = kids,
    upvoted = upvoted,
    favourited = favourited,
    flagged = flagged,
    expanded = expanded,
    followed = followed,
)
