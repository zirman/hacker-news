package com.monoid.hackernews.common.data.api

import com.monoid.hackernews.common.data.model.Settings
import com.monoid.hackernews.common.data.model.Username
import io.ktor.client.HttpClient
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

internal const val Y_COMBINATOR_BASE_URL = "https://news.ycombinator.com"

suspend fun HttpClient.registerRequest(settings: Settings) {
    yCombRequest(
        path = "login",
        httpStatusCode = HttpStatusCode.Found,
        settings = settings,
    ) {
        append("creating", "t")
    }
}

suspend fun HttpClient.loginRequest(settings: Settings) {
    yCombRequest(
        path = "login",
        httpStatusCode = HttpStatusCode.Found,
        settings = settings,
    )
}

suspend fun HttpClient.favoriteRequest(
    settings: Settings,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        path = "fave",
        httpStatusCode = HttpStatusCode.Found,
        settings = settings,
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
        path = "flag",
        httpStatusCode = HttpStatusCode.Found,
        settings = settings,
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
        path = "vote",
        httpStatusCode = HttpStatusCode.Found,
        settings = settings,
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
        path = "comment",
        httpStatusCode = HttpStatusCode.Found,
        settings = settings,
    ) {
        append("parent", parentId.long.toString())
        append("text", text)
    }
}

suspend fun HttpClient.getFavorites(username: Username, page: Int = 0): String =
    getPage(path = "favorites", username = username, page = page)

suspend fun HttpClient.getSubmissions(username: Username, page: Int = 0): String =
    getPage(path = "submitted", username = username, page = page)

suspend fun HttpClient.getComments(username: Username, page: Int = 0): String =
    getPage(path = "threads", username = username, page = page)

private suspend fun HttpClient.getPage(path: String, username: Username, page: Int = 0): String =
    yCombRequest(path = path) {
        append("id", username.string)
        if (page > 0) append("p", page.toString())
    }.bodyAsText()

suspend fun HttpClient.getUpvoted(
    preferences: Settings,
    username: Username,
    page: Int = 0,
): String {
    return yCombRequest(path = "upvoted") {
        append("id", username.string)
        if (page > 0) append("p", page.toString())
        append("acct", preferences.username.string)
        append("pw", preferences.password.string)
    }.bodyAsText()
}
