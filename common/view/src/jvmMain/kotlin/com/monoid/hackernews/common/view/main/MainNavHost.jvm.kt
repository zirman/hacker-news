package com.monoid.hackernews.common.view.main

import com.monoid.hackernews.common.data.URI
import com.monoid.hackernews.common.data.URL
import java.awt.Desktop

fun openWebpage(uri: URI): Boolean =
    (if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null)
        ?.takeIf { it.isSupported(Desktop.Action.BROWSE) }
        ?.run {
            browse(uri.uri)
            true
        }
        ?: false

actual fun openWebpage(url: URL): Boolean = runCatching { openWebpage(url.toUri()) }
    .getOrDefault(false)
