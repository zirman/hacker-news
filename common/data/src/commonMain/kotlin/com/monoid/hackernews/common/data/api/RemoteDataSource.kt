package com.monoid.hackernews.common.data.api

import com.monoid.hackernews.common.data.model.Username
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val HACKER_NEWS_API_BASE = "https://hacker-news.firebaseio.com/v0/"

suspend fun HttpClient.getTopStories(): List<Long> {
    return get(urlString = "${HACKER_NEWS_API_BASE}topstories.json").body()
}

suspend fun HttpClient.getNewStories(): List<Long> {
    return get(urlString = "${HACKER_NEWS_API_BASE}newstories.json").body()
}

suspend fun HttpClient.getBestStories(): List<Long> {
    return get(urlString = "${HACKER_NEWS_API_BASE}beststories.json").body()
}

suspend fun HttpClient.getShowStories(): List<Long> {
    return get(urlString = "${HACKER_NEWS_API_BASE}showstories.json").body()
}

suspend fun HttpClient.getAskStories(): List<Long> {
    return get(urlString = "${HACKER_NEWS_API_BASE}askstories.json").body()
}

suspend fun HttpClient.getJobStories(): List<Long> {
    return get(urlString = "${HACKER_NEWS_API_BASE}jobstories.json").body()
}

suspend fun HttpClient.getItem(itemId: ItemId): ItemApi {
    return get(urlString = "${HACKER_NEWS_API_BASE}item/${itemId.long}.json").body()
}

suspend fun HttpClient.getUser(username: Username): UserApi {
    return get(urlString = "${HACKER_NEWS_API_BASE}user/${username.string}.json").body()
}
