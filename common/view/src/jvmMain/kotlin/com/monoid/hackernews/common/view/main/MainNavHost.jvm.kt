package com.monoid.hackernews.common.view.main

import com.monoid.hackernews.common.data.Uri
import com.monoid.hackernews.common.data.Url
import java.awt.Desktop

fun openWebpage(uri: Uri): Boolean =
    (if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null)
        ?.takeIf { it.isSupported(Desktop.Action.BROWSE) }
        ?.run {
            browse(uri.uri)
            true
        }
        ?: false

actual fun openWebpage(url: Url): Boolean = runCatching { openWebpage(url.toUri()) }
    .getOrDefault(false)
