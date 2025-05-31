package com.monoid.hackernews.common.data

import platform.Foundation.NSURL

actual class Url actual constructor(path: String) {
    val nsUrl = requireNotNull(NSURL.URLWithString(path))
    actual fun toUri(): Uri = Uri(requireNotNull(nsUrl.host))

    actual val host: String
        get() = requireNotNull(nsUrl.host)
}
