package com.monoid.hackernews.common.api

import com.monoid.hackernews.common.data.Username
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val firebaseApiBase = "https://hacker-news.firebaseio.com/v0/"

suspend fun HttpClient.getTopStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}topstories.json").body()
}

suspend fun HttpClient.getNewStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}newstories.json").body()
}

suspend fun HttpClient.getBestStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}beststories.json").body()
}

suspend fun HttpClient.getShowStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}showstories.json").body()
}

suspend fun HttpClient.getAskStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}askstories.json").body()
}

suspend fun HttpClient.getJobStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}jobstories.json").body()
}

suspend fun HttpClient.getItem(itemId: ItemId): ItemApi {
    return get(urlString = "${firebaseApiBase}item/${itemId.long}.json").body()
}

suspend fun HttpClient.getUser(username: Username): UserApi {
    return get(urlString = "${firebaseApiBase}user/${username.string}.json").body()
}
