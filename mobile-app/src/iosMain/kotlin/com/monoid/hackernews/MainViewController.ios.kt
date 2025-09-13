package com.monoid.hackernews

import androidx.compose.ui.window.ComposeUIViewController
import com.monoid.hackernews.common.view.App
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Suppress("unused")
fun mainViewController() = ComposeUIViewController {
    App(
        onClickUrl = { url ->
            UIApplication.sharedApplication.openURL(
                url = checkNotNull(NSURL.URLWithString(url.toString())),
                options = mapOf<Any?, Nothing>(),
                completionHandler = {},
            )
        },
    )
}
