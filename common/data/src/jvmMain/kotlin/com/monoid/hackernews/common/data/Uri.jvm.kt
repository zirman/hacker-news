package com.monoid.hackernews.common.data

actual class Uri(val uri: java.net.URI) {
    actual constructor(path: String) : this(java.net.URI(path))

    override fun toString(): String = uri.toString()
}
