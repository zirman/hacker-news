@file:OptIn(ExperimentalNativeApi::class)

package com.monoid.hackernews.common.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.retain.retain
import com.monoid.hackernews.common.IosAppGraph
import com.monoid.hackernews.common.core.metro.LocalViewModelProviderFactory
import com.monoid.hackernews.common.view.stories.LocalPlatformContext
import com.monoid.hackernews.common.view.stories.PlatformContext
import dev.zacsweers.metro.createGraph
import io.ktor.http.Url
import kotlin.experimental.ExperimentalNativeApi

@Composable
fun IosApp(onClickUrl: (Url) -> Unit) {
    val appGraph = retain { createGraph<IosAppGraph>() }
    CompositionLocalProvider(
        LocalViewModelProviderFactory provides appGraph.iosViewModelFactory,
        LocalPlatformContext provides PlatformContext(Unit),
    ) {
        MainApp(onClickUrl)
    }
}
