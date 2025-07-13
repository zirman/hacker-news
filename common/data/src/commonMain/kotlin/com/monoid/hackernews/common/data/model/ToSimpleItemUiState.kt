package com.monoid.hackernews.common.data.model

import com.monoid.hackernews.common.data.api.ItemApi
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.html.toHtmlAnnotatedString
import com.monoid.hackernews.common.data.room.EXPANDED_DEFAULT
import com.monoid.hackernews.common.data.room.FOLLOWED_DEFAULT
import com.monoid.hackernews.common.data.room.ItemDb
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.path
import kotlin.time.Instant

fun ItemDb.toSimpleItemUiState(kids: List<ItemId>): Item = Item(
    id = ItemId(id),
    lastUpdate = lastUpdate,
    kids = kids,
    type = type,
    time = time,
    deleted = deleted,
    by = by?.let { Username(it) },
    descendants = descendants,
    score = score,
    title = title,
    text = text?.toHtmlAnnotatedString(),
    url = url?.let { Url(it) },
    favicon = url
        ?.let { URLBuilder(it) }
        ?.apply {
            path("favicon.ico")
            parameters.clear()
        }
        ?.build(),
    parent = parent?.let { ItemId(it) },
    upvoted = upvoted,
    favorited = favorited,
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
            Item(
                id = id,
                lastUpdate = lastUpdate,
                kids = kids,
                type = ItemType.Comment,
                time = time,
                deleted = deleted,
                by = by?.let { Username(it) },
                text = text?.toHtmlAnnotatedString(),
                parent = parent,
                expanded = expanded,
                followed = followed,
            )
        }

        is ItemApi.Job -> {
            Item(
                id = id,
                lastUpdate = lastUpdate,
                kids = kids,
                type = ItemType.Job,
                time = time,
                deleted = deleted,
                by = by?.let { Username(it) },
                title = title,
                text = text?.toHtmlAnnotatedString(),
                url = url?.let { Url(it) },
                favicon = url
                    ?.let { URLBuilder(it) }
                    ?.apply {
                        path("favicon.ico")
                        parameters.clear()
                    }
                    ?.build(),
                expanded = expanded,
                followed = followed,
            )
        }

        is ItemApi.Poll -> {
            Item(
                id = id,
                lastUpdate = lastUpdate,
                kids = kids,
                type = ItemType.Poll,
                time = time,
                deleted = deleted,
                by = by?.let { Username(it) },
                descendants = descendants,
                score = score,
                title = title,
                expanded = expanded,
                followed = followed,
            )
        }

        is ItemApi.PollOpt -> {
            Item(
                id = id,
                lastUpdate = lastUpdate,
                kids = kids,
                type = ItemType.PollOpt,
                time = time,
                deleted = deleted,
                by = by?.let { Username(it) },
                score = score,
                title = title,
                expanded = expanded,
                followed = followed,
            )
        }

        is ItemApi.Story -> {
            Item(
                id = id,
                lastUpdate = lastUpdate,
                kids = kids,
                type = ItemType.Story,
                time = time,
                deleted = deleted,
                by = by?.let { Username(it) },
                descendants = descendants,
                score = score,
                title = title,
                text = text?.toHtmlAnnotatedString(),
                url = url?.let { Url(it) },
                favicon = url
                    ?.let { URLBuilder(it) }
                    ?.apply {
                        path("favicon.ico")
                        parameters.clear()
                    }
                    ?.build(),
                expanded = expanded,
                followed = followed,
            )
        }
    }
}
