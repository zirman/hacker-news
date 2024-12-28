package com.monoid.hackernews.common.view.itemlist

import platform.Foundation.NSURL

actual class URL actual constructor(string: String) {
    private val nsUrl = requireNotNull(NSURL.URLWithString(string))

    actual val host: String
        get() = requireNotNull(nsUrl.host)
}
