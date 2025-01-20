package com.monoid.hackernews.common.data.model

import com.monoid.hackernews.common.data.api.ItemApi
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.html.toHtmlAnnotatedString
import com.monoid.hackernews.common.data.room.EXPANDED_DEFAULT
import com.monoid.hackernews.common.data.room.FOLLOWED_DEFAULT
import com.monoid.hackernews.common.data.room.ItemDb
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
    title = title?.toHtmlAnnotatedString(),
    text = text?.toHtmlAnnotatedString(),
    url = url,
    parent = parent?.let { ItemId(it) },
    upvoted = upvoted,
    favourited = favourited,
    flagged = flagged,
    expanded = expanded,
    followed = followed,
)

fun ItemApi.toSimpleItemUiState(instant: Instant, item: Item?): Item {
    val lastUpdate = instant.epochSeconds
    // ensure kids are sorted by id
    val kids = kids?.sortedBy { it.long }
    val expanded = item?.expanded ?: EXPANDED_DEFAULT
    val followed = item?.followed ?: FOLLOWED_DEFAULT
    return when (this) {
        is ItemApi.Comment -> {
            makeItem(
                id = id,
                lastUpdate = lastUpdate,
                kids = kids,
                type = ItemType.Comment,
                time = time,
                deleted = deleted,
                by = by,
                text = text?.toHtmlAnnotatedString(),
                parent = parent,
                expanded = expanded,
                followed = followed,
            )
        }

        is ItemApi.Job -> {
            val title = title?.toHtmlAnnotatedString()
            val text = text?.toHtmlAnnotatedString()
            makeItem(
                id = id,
                lastUpdate = lastUpdate,
                kids = kids,
                type = ItemType.Job,
                time = time,
                deleted = deleted,
                by = by,
                title = title,
                text = text,
                url = url,
                expanded = expanded,
                followed = followed,
            )
        }

        is ItemApi.Poll -> {
            val title = title?.toHtmlAnnotatedString()
            makeItem(
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
                expanded = expanded,
                followed = followed,
            )
        }

        is ItemApi.PollOpt -> {
            val title = title?.toHtmlAnnotatedString()
            makeItem(
                id = id,
                lastUpdate = lastUpdate,
                kids = kids,
                type = ItemType.PollOpt,
                time = time,
                deleted = deleted,
                by = by,
                score = score,
                title = title,
                expanded = expanded,
                followed = followed,
            )
        }

        is ItemApi.Story -> {
            val title = title?.toHtmlAnnotatedString()
            val text = text?.toHtmlAnnotatedString()
            makeItem(
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
                text = text,
                url = url,
                expanded = expanded,
                followed = followed,
            )
        }
    }
}
