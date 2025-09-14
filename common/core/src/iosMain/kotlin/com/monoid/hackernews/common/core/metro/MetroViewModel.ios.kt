package com.monoid.hackernews.common.core.metro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider

val LocalIosViewModelProviderFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("CompositionLocal LocalIosViewModelProviderFactory not present")
}

@Composable
actual fun metroViewModelProviderFactory(): ViewModelProvider.Factory =
    LocalIosViewModelProviderFactory.current
