package com.monoid.hackernews.common.api

import com.monoid.hackernews.common.data.Username
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val hackernewsApiBase = "https://hacker-news.firebaseio.com/v0/"

suspend fun HttpClient.getTopStories(): List<Long> {
    return get(urlString = "${hackernewsApiBase}topstories.json").body()
}

suspend fun HttpClient.getNewStories(): List<Long> {
    return get(urlString = "${hackernewsApiBase}newstories.json").body()
}

suspend fun HttpClient.getBestStories(): List<Long> {
    return get(urlString = "${hackernewsApiBase}beststories.json").body()
}

suspend fun HttpClient.getShowStories(): List<Long> {
    return get(urlString = "${hackernewsApiBase}showstories.json").body()
}

suspend fun HttpClient.getAskStories(): List<Long> {
    return get(urlString = "${hackernewsApiBase}askstories.json").body()
}

suspend fun HttpClient.getJobStories(): List<Long> {
    return get(urlString = "${hackernewsApiBase}jobstories.json").body()
}

suspend fun HttpClient.getItem(itemId: ItemId): ItemApi {
    return get(urlString = "${hackernewsApiBase}item/${itemId.long}.json").body()
}

suspend fun HttpClient.getUser(username: Username): UserApi {
    return get(urlString = "${hackernewsApiBase}user/${username.string}.json").body()
}
