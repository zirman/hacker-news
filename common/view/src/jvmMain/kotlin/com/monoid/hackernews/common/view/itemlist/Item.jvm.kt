package com.monoid.hackernews.common.view.itemlist

actual class URL actual constructor(string: String) {
    @Suppress("DEPRECATION")
    private val netUrl = java.net.URL(string)

    actual val host: String
        get() = netUrl.host
}
