package com.monoid.hackernews.api

import com.monoid.hackernews.Username
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val firebaseApiBase = "https://hacker-news.firebaseio.com/v0/"

suspend fun HttpClient.getTopStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}topstories.json")
}

suspend fun HttpClient.getNewStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}newstories.json")
}

suspend fun HttpClient.getBestStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}beststories.json")
}

suspend fun HttpClient.getShowStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}showstories.json")
}

suspend fun HttpClient.getAskStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}askstories.json")
}

suspend fun HttpClient.getJobStories(): List<Long> {
    return get(urlString = "${firebaseApiBase}jobstories.json")
}

suspend fun HttpClient.getItem(itemId: ItemId): Item {
    return get(urlString = "${firebaseApiBase}item/${itemId.long}.json")
}

suspend fun HttpClient.getUser(username: Username): User {
    return get(urlString = "${firebaseApiBase}user/${username.string}.json")
}
