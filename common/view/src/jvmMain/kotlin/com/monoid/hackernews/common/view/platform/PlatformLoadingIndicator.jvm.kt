@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.common.view.platform

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformLoadingIndicator(modifier: Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
actual fun PlatformPullToRefreshIndicator(state: PullToRefreshState, isRefreshing: Boolean, modifier: Modifier) {
    PullToRefreshDefaults.Indicator(
        state = state,
        isRefreshing = isRefreshing,
        modifier = modifier,
    )
}
