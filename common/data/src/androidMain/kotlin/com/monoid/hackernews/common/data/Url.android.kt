package com.monoid.hackernews.common.data

actual class Url(private val url: java.net.URL) {
    actual constructor(path: String) : this(java.net.URL(path))

    override fun toString(): String = url.toString()

    actual fun toUri(): Uri = Uri(url.toURI())

    actual val host: String
        get() = url.host
}
