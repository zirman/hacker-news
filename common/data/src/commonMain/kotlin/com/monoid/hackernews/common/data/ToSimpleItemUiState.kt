package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemApi
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.room.ItemDb
import kotlinx.datetime.Instant

fun ItemDb.toSimpleItemUiState(kids: List<ItemId>): SimpleItemUiState = makeSimpleItemUiState(
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
    text = title,
    url = url,
    parent = parent?.let { ItemId(it) },
)

fun ItemApi.toSimpleItemUiState(instant: Instant): SimpleItemUiState {
    val lastUpdate = instant.epochSeconds
    return when (this) {
        is ItemApi.Comment -> makeSimpleItemUiState(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = "comment",
            time = time,
            deleted = deleted,
            by = by,
            parent = parent,
        )

        is ItemApi.Job -> makeSimpleItemUiState(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = "job",
            time = time,
            deleted = deleted,
            by = by,
            title = title,
            text = title,
            url = url,
        )

        is ItemApi.Poll -> makeSimpleItemUiState(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = "poll",
            time = time,
            deleted = deleted,
            by = by,
            descendants = descendants,
            score = score,
            title = title,
            text = title,
        )

        is ItemApi.PollOpt -> makeSimpleItemUiState(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = "poll_opt",
            time = time,
            deleted = deleted,
            by = by,
            score = score,
            title = title,
            text = title,
        )

        is ItemApi.Story -> makeSimpleItemUiState(
            id = id,
            lastUpdate = lastUpdate,
            kids = kids,
            type = "story",
            time = time,
            deleted = deleted,
            by = by,
            descendants = descendants,
            score = score,
            title = title,
            text = title,
            url = url,
        )
    }
}
