package com.monoid.hackernews.common.domain.navigation

import java.net.URLDecoder
import java.net.URLEncoder

actual fun encodeUrl(str: String): String {
    return URLEncoder.encode(str, "utf-8")
}

actual fun decodeUrl(str: String): String {
    return URLDecoder.decode(str, "utf-8")
}
