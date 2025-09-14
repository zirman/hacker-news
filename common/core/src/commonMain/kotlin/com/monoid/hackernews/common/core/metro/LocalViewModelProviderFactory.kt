package com.monoid.hackernews.common.core.metro

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider

@Suppress("ComposeCompositionLocalUsage")
val LocalViewModelProviderFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("CompositionLocal LocalViewModelProviderFactory not present")
}
