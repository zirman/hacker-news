package com.monoid.hackernews.common.view.itemlist

import platform.Foundation.NSURL

actual class URL actual constructor(string: String) {
    private val url = NSURL.URLWithString(string)!!

    actual val host: String
        get() = url.host!!
}
