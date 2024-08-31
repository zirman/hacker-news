package com.monoid.hackernews.view.main

import com.monoid.hackernews.common.data.URI
import com.monoid.hackernews.common.data.URL
import java.awt.Desktop
import java.net.URISyntaxException

fun openWebpage(uri: URI?): Boolean {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(uri!!.uri)
            return true
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
    return false
}

actual fun openWebpage(url: URL): Boolean {
    try {
        return openWebpage(url.toUri())
    } catch (e: URISyntaxException) {
        e.printStackTrace()
    }
    return false
}
