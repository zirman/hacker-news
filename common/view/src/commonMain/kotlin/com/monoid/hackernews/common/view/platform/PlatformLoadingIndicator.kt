@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.common.view.platform

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformLoadingIndicator(modifier: Modifier = Modifier)

@Composable
expect fun PlatformPullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
)
