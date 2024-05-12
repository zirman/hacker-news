package com.monoid.hackernews.common.api

import com.monoid.hackernews.common.data.Authentication
import com.monoid.hackernews.common.data.Username
import io.ktor.client.HttpClient

class YCombException(message: String? = null) : Exception(message)
class WebViewException : Exception()

internal const val yCombinatorBaseUrl = "https://news.ycombinator.com"

suspend fun HttpClient.registerRequest(authentication: Authentication) {
    yCombRequest(
        authentication = authentication,
        path = "login",
    ) {
        append("creating", "t")
    }
}

suspend fun HttpClient.loginRequest(authentication: Authentication) {
    yCombRequest(
        authentication = authentication,
        path = "login",
    )
}

suspend fun HttpClient.favoriteRequest(
    authentication: Authentication,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        authentication = authentication,
        path = "fave",
    ) {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.flagRequest(
    authentication: Authentication,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        authentication = authentication,
        path = "flag",
    ) {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.upvoteItem(
    authentication: Authentication,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        authentication = authentication,
        path = "vote",
    ) {
        append("id", itemId.long.toString())
        append("how", if (flag) "up" else "un")
    }
}

suspend fun HttpClient.commentRequest(
    authentication: Authentication,
    parentId: ItemId,
    text: String,
) {
    yCombRequest(
        authentication = authentication,
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
    authentication: Authentication,
    username: Username,
): List<ItemId> {
    return getHtmlItems(
        path = "upvoted?id=${username.string}&acct=${authentication.username}&pw=${authentication.password}",
    )
}
