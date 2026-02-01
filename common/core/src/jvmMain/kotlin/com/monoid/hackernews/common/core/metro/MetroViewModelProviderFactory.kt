package com.monoid.hackernews.common.core.metro

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider

@Composable
actual fun metroViewModelProviderFactory(): ViewModelProvider.Factory = LocalViewModelProviderFactory.current
