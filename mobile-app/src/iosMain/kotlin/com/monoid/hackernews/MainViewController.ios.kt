package com.monoid.hackernews

import androidx.compose.ui.window.ComposeUIViewController
import com.monoid.hackernews.common.view.App

@Suppress("unused")
fun MainViewController() = ComposeUIViewController(
    configure = {
    },
) {
    App(
        onClickUrl = {
            // TODO
        },
    )
}
