package com.monoid.hackernews.common

expect class URL(path: String) {
    fun toUri(): URI
}
