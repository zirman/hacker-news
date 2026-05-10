package com.monoid.hackernews.common.data

import org.w3c.dom.url.URL

actual class Uri(val url: URL) {
    actual constructor(path: String) : this(URL(path))

    override fun toString(): String = url.toString()
}
