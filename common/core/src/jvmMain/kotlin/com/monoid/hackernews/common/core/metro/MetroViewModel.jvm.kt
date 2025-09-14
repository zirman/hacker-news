package com.monoid.hackernews.common.core.metro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider

val LocalJvmViewModelProviderFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("CompositionLocal LocalJvmViewModelProviderFactory not present")
}

@Composable
actual fun metroViewModelProviderFactory(): ViewModelProvider.Factory =
    LocalJvmViewModelProviderFactory.current
