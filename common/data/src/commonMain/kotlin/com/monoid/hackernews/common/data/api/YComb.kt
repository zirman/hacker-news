package com.monoid.hackernews.common.data.api

import com.monoid.hackernews.common.data.model.Settings
import com.monoid.hackernews.common.data.model.Username
import io.ktor.client.HttpClient

internal const val Y_COMBINATOR_BASE_URL = "https://news.ycombinator.com"

suspend fun HttpClient.registerRequest(settings: Settings) {
    yCombRequest(
        settings = settings,
        path = "login",
    ) {
        append("creating", "t")
    }
}

suspend fun HttpClient.loginRequest(settings: Settings) {
    yCombRequest(
        settings = settings,
        path = "login",
    )
}

suspend fun HttpClient.favoriteRequest(
    settings: Settings,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        settings = settings,
        path = "fave",
    ) {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.flagRequest(
    settings: Settings,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        settings = settings,
        path = "flag",
    ) {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.upvoteItem(
    settings: Settings,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        settings = settings,
        path = "vote",
    ) {
        append("id", itemId.long.toString())
        append("how", if (flag) "up" else "un")
    }
}

suspend fun HttpClient.commentRequest(
    settings: Settings,
    parentId: ItemId,
    text: String,
) {
    yCombRequest(
        settings = settings,
        path = "comment",
    ) {
        append("parent", parentId.long.toString())
        append("text", text)
    }
}

suspend fun getFavorites(username: Username): List<ItemId> {
    return getHtmlItems(path = "favorites?id=${username.string}")
}

suspend fun getSubmissions(username: Username): List<ItemId> {
    return getHtmlItems(path = "submitted?id=${username.string}")
}

suspend fun getComments(username: Username): List<ItemId> {
    return getHtmlItems(path = "threads?id=${username.string}")
}

suspend fun getUpvoted(
    preferences: Settings,
    username: Username,
): List<ItemId> {
    return getHtmlItems(
        path = "upvoted?id=${username.string}&acct=${preferences.username}&pw=${preferences.password}",
    )
}
