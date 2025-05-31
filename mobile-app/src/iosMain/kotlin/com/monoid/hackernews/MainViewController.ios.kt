package com.monoid.hackernews

import androidx.compose.ui.window.ComposeUIViewController
import com.monoid.hackernews.common.view.App
import platform.UIKit.UIApplication

@Suppress("unused")
fun MainViewController() = ComposeUIViewController {
    App(
        onClickUrl = {
            UIApplication.sharedApplication.openURL(
                url = it.nsUrl,
                options = mapOf<Any?, Nothing>(),
                completionHandler = {},
            )
        },
    )
}
