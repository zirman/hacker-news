package com.monoid.hackernews.common.view.itemlist

actual class URL actual constructor(string: String) {
    private val url = java.net.URL(string)

    actual val host: String
        get() = url.host
}
