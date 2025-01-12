package com.monoid.hackernews.common.data

expect class Url(path: String) {
    fun toUri(): Uri
    val host: String
}
