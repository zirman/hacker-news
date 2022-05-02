@file:Suppress("unused")

package com.monoid.hackernews.api

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ItemId(val long: Long)

@Serializable
data class User(
    val id: String,
    val created: Long,
    val about: String? = null,
    val karma: Int,
    val submitted: List<ItemId> = emptyList(),
)

fun User.toRoomUser(
    lastUpdate: Long = Clock.System.now().epochSeconds,
): com.monoid.hackernews.room.User {
    return com.monoid.hackernews.room.User(
        id = id,
        lastUpdate = lastUpdate,
        created = created,
        karma = karma,
        about = about,
    )
}

@Serializable
sealed class Item(
    val deleted: Boolean = false,
    val kids: List<ItemId>? = null,
) {
    abstract val id: ItemId
    abstract val time: Long?

    @Serializable
    @SerialName("story")
    data class Story(
        override val id: ItemId,
        override val time: Long? = null,
        val by: String? = null,
        val descendants: Int? = null,
        val score: Int? = null,
        val title: String? = null,
        val text: String? = null,
        val url: String? = null
    ) : Item()

    @Serializable
    @SerialName("job")
    data class Job(
        override val id: ItemId,
        override val time: Long? = null,
        val by: String? = null,
        val title: String? = null,
        val text: String? = null,
        val url: String? = null,
    ) : Item()

    @Serializable
    @SerialName("poll")
    data class Poll(
        override val id: ItemId,
        override val time: Long? = null,
        val by: String? = null,
        val parts: List<ItemId>,
        val descendants: Int? = null,
        val score: Int? = null,
        val title: String? = null,
    ) : Item()

    @Serializable
    @SerialName("pollopt")
    data class PollOpt(
        override val id: ItemId,
        override val time: Long? = null,
        val poll: ItemId,
        val by: String? = null,
        val score: Int? = null,
        val title: String? = null,
    ) : Item()

    @Serializable
    @SerialName("comment")
    data class Comment(
        override val id: ItemId,
        override val time: Long? = null,
        val by: String? = null,
        val parent: ItemId,
        val text: String? = null,
    ) : Item()
}

fun Item.toRoomItem(
    lastUpdate: Long? = Clock.System.now().epochSeconds,
): com.monoid.hackernews.room.Item {
    return when (this) {
        is Item.Comment -> {
            com.monoid.hackernews.room.Item(
                id = id.long,
                lastUpdate = lastUpdate,
                type = "comment",
                time = time,
                deleted = deleted,
                by = by,
                text = text,
                parent = parent.long,
            )
        }
        is Item.Job -> {
            com.monoid.hackernews.room.Item(
                id = id.long,
                lastUpdate = lastUpdate,
                type = "job",
                time = time,
                deleted = deleted,
                by = by,
                title = title,
                text = text,
                url = url,
            )
        }
        is Item.Poll -> {
            com.monoid.hackernews.room.Item(
                id = id.long,
                lastUpdate = lastUpdate,
                type = "poll",
                time = time,
                deleted = deleted,
                by = by,
                descendants = descendants,
                score = score,
                title = title,
            )
        }
        is Item.PollOpt -> {
            com.monoid.hackernews.room.Item(
                id = id.long,
                lastUpdate = lastUpdate,
                type = "pollopt",
                time = time,
                deleted = deleted,
                by = by,
                score = score,
                title = title,
            )
        }
        is Item.Story -> {
            com.monoid.hackernews.room.Item(
                id = id.long,
                lastUpdate = lastUpdate,
                type = "story",
                time = time,
                deleted = deleted,
                by = by,
                descendants = descendants,
                score = score,
                title = title,
                text = text,
                url = url,
            )
        }
    }
}
