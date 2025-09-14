package com.monoid.hackernews

import androidx.compose.ui.window.ComposeUIViewController
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Suppress("unused")
fun mainViewController() = ComposeUIViewController {
    IosApp(
        onClickUrl = { url ->
            UIApplication.sharedApplication.openURL(
                url = checkNotNull(NSURL.URLWithString(url.toString())),
                options = mapOf<Any?, Nothing>(),
                completionHandler = {},
            )
        },
    )
}
