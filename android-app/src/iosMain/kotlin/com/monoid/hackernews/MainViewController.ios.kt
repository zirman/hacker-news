@file:OptIn(ExperimentalComposeApi::class)

package com.monoid.hackernews

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.platform.AccessibilitySyncOptions
import androidx.compose.ui.window.ComposeUIViewController

@Suppress("unused")
fun MainViewController() = ComposeUIViewController(
    configure = {
        accessibilitySyncOptions = AccessibilitySyncOptions.Always(null)
    },
) { App() }
