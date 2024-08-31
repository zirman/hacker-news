package com.monoid.hackernews.common.data

expect class URL(path: String) {
    fun toUri(): URI
}
