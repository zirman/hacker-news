package com.monoid.hackernews.common.view.itemlist

actual class Url actual constructor(string: String) {
    private val javaNetUrl = java.net.URL(string)

    actual val host: String
        get() = javaNetUrl.host
}
