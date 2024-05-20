package com.monoid.hackernews.common.api

import com.monoid.hackernews.common.data.Preferences
import com.monoid.hackernews.common.data.Username
import io.ktor.client.HttpClient

class YCombException(message: String? = null) : Exception(message)
class WebViewException : Exception()

internal const val yCombinatorBaseUrl = "https://news.ycombinator.com"

suspend fun HttpClient.registerRequest(preferences: Preferences) {
    yCombRequest(
        preferences = preferences,
        path = "login",
    ) {
        append("creating", "t")
    }
}

suspend fun HttpClient.loginRequest(preferences: Preferences) {
    yCombRequest(
        preferences = preferences,
        path = "login",
    )
}

suspend fun HttpClient.favoriteRequest(
    preferences: Preferences,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        preferences = preferences,
        path = "fave",
    ) {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.flagRequest(
    preferences: Preferences,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        preferences = preferences,
        path = "flag",
    ) {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.upvoteItem(
    preferences: Preferences,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        preferences = preferences,
        path = "vote",
    ) {
        append("id", itemId.long.toString())
        append("how", if (flag) "up" else "un")
    }
}

suspend fun HttpClient.commentRequest(
    preferences: Preferences,
    parentId: ItemId,
    text: String,
) {
    yCombRequest(
        preferences = preferences,
        path = "comment",
    ) {
        append("parent", parentId.long.toString())
        append("text", text)
    }
}

suspend fun getFavorites(username: Username): List<ItemId> {
    return getHtmlItems(path = "favorites?id=${username.string}")
}

suspend fun getSubmissions( username: Username): List<ItemId> {
    return getHtmlItems(path = "submitted?id=${username.string}")
}

suspend fun getComments(username: Username): List<ItemId> {
    return getHtmlItems(path = "threads?id=${username.string}")
}

suspend fun getUpvoted(
    preferences: Preferences,
    username: Username,
): List<ItemId> {
    return getHtmlItems(
        path = "upvoted?id=${username.string}&acct=${preferences.username}&pw=${preferences.password}",
    )
}
