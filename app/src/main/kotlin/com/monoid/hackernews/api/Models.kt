@file:Suppress("unused")

package com.monoid.hackernews.api

import com.monoid.hackernews.room.ItemDb
import com.monoid.hackernews.room.UserDb
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ItemId(val long: Long)

@Serializable
data class UserApi(
    val id: String,
    val created: Long,
    val about: String? = null,
    val karma: Int,
    val submitted: List<ItemId> = emptyList(),
)

fun UserApi.toUserApiUpdate(
    lastUpdate: Long = Clock.System.now().epochSeconds,
): UserDb {
    return UserDb(
        id = id,
        lastUpdate = lastUpdate,
        created = created,
        karma = karma,
        about = about,
    )
}

@Serializable
sealed class ItemApi(
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
    ) : ItemApi()

    @Serializable
    @SerialName("job")
    data class Job(
        override val id: ItemId,
        override val time: Long? = null,
        val by: String? = null,
        val title: String? = null,
        val text: String? = null,
        val url: String? = null,
    ) : ItemApi()

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
    ) : ItemApi()

    @Serializable
    @SerialName("pollopt")
    data class PollOpt(
        override val id: ItemId,
        override val time: Long? = null,
        val poll: ItemId,
        val by: String? = null,
        val score: Int? = null,
        val title: String? = null,
    ) : ItemApi()

    @Serializable
    @SerialName("comment")
    data class Comment(
        override val id: ItemId,
        override val time: Long? = null,
        val by: String? = null,
        val parent: ItemId,
        val text: String? = null,
    ) : ItemApi()
}

fun ItemApi.toItemDb(
    lastUpdate: Long? = Clock.System.now().epochSeconds,
): ItemDb {
    return when (this) {
        is ItemApi.Comment -> {
            ItemDb(
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
        is ItemApi.Job -> {
            ItemDb(
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
        is ItemApi.Poll -> {
            ItemDb(
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
        is ItemApi.PollOpt -> {
            ItemDb(
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
        is ItemApi.Story -> {
            ItemDb(
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
