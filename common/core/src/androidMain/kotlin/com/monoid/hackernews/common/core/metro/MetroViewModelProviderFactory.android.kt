package com.monoid.hackernews.common.core.metro

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider

@Composable
actual fun metroViewModelProviderFactory(): ViewModelProvider.Factory =
    (LocalActivity.current as HasDefaultViewModelProviderFactory)
        .defaultViewModelProviderFactory
