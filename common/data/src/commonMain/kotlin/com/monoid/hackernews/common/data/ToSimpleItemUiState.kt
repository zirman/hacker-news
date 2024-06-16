package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemApi
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.room.ItemDb
import kotlinx.datetime.Instant

fun ItemDb.toSimpleItemUiState(kids: List<ItemId>): Item = makeItem(
    id = ItemId(id),
    lastUpdate = lastUpdate,
    kids = kids,
    type = type,
    time = time,
    deleted = deleted,
    by = by,
    descendants = descendants,
    score = score,
    title = title,
    text = text,
    url = url,
    parent = parent?.let { ItemId(it) },
    upvoted = upvoted,
    favourited = favourited,
    flagged = flagged,
    expanded = expanded,
    followed = followed,
)

fun ItemApi.toSimpleItemUiState(instant: Instant): Item {
    val lastUpdate = instant.epochSeconds
    return when (this) {
        is ItemApi.Comment -> makeItem(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = ItemType.Comment,
            time = time,
            deleted = deleted,
            by = by,
            text = text,
            parent = parent,
            expanded = true,
            followed = false,
        )

        is ItemApi.Job -> makeItem(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = ItemType.Job,
            time = time,
            deleted = deleted,
            by = by,
            title = title,
            text = title,
            url = url,
            expanded = true,
            followed = false,
        )

        is ItemApi.Poll -> makeItem(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = ItemType.Poll,
            time = time,
            deleted = deleted,
            by = by,
            descendants = descendants,
            score = score,
            title = title,
            text = title,
            expanded = true,
            followed = false,
        )

        is ItemApi.PollOpt -> makeItem(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = ItemType.PollOpt,
            time = time,
            deleted = deleted,
            by = by,
            score = score,
            title = title,
            text = title,
            expanded = true,
            followed = false,
        )

        is ItemApi.Story -> makeItem(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = ItemType.Story,
            time = time,
            deleted = deleted,
            by = by,
            descendants = descendants,
            score = score,
            title = title,
            text = title,
            url = url,
            expanded = true,
            followed = false,
        )
    }
}
