@file:OptIn(ExperimentalComposeApi::class)

package com.monoid.hackernews

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.window.ComposeUIViewController
import com.monoid.hackernews.common.view.App

@Suppress("unused")
fun MainViewController() = ComposeUIViewController(
    configure = {
        // accessibilitySyncOptions = AccessibilitySyncOptions.Always
    },
) {
    App(
        onClickUrl = {
            // TODO
        },
    )
}
