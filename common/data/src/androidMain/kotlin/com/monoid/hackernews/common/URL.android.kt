package com.monoid.hackernews.common

actual class URL(private val url: java.net.URL) {
    actual constructor(path: String) : this(java.net.URL(path))

    override fun toString(): String = url.toString()

    actual fun toUri(): URI {
        return URI(url.toURI())
    }
}
