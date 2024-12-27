package com.monoid.hackernews.common.data

import platform.Foundation.NSURL

actual class URL actual constructor(path: String) {
    private val url = NSURL.URLWithString(path)!!
    actual fun toUri(): URI {
        return URI(url.host!!)
    }
}
