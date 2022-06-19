package com.monoid.hackernews.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.metrics.performance.PerformanceMetricsState

@Composable
fun rememberMetricsStateHolder(): PerformanceMetricsState.MetricsStateHolder {
    val view = LocalView.current
    return remember(view) { PerformanceMetricsState.getForHierarchy(view) }
}
