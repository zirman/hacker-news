package com.monoid.hackernews.common.view

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.monoid.hackernews.common.core.metro.LocalViewModelProviderFactory
import com.monoid.hackernews.common.view.stories.LocalPlatformContext
import com.monoid.hackernews.common.view.stories.PlatformContext
import dev.zacsweers.metro.createGraph

@OptIn(ExperimentalComposeUiApi::class)
fun webMain() {
    val appGraph = createGraph<WebAppGraph>()
    ComposeViewport {
        CompositionLocalProvider(
            LocalViewModelProviderFactory provides appGraph.webViewModelFactory,
            LocalPlatformContext provides PlatformContext(Unit),
        ) {
            WebApp(onClickUrl = { /* TODO */ })
        }
    }
}
