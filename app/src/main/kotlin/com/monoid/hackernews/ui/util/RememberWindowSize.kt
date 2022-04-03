package com.monoid.hackernews.ui.util

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator

enum class WindowSizeClass {
    Compact,
    Medium,
    Expanded,
}

data class WindowSize(val width: WindowSizeClass, val height: WindowSizeClass)

@Composable
fun rememberWindowSize(): WindowSize {
    val activity: Activity =
        LocalContext.current as Activity

    val configuration: Configuration =
        LocalConfiguration.current

    val windowMetrics: WindowMetrics =
        remember(configuration.screenWidthDp, configuration.screenHeightDp) {
            WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(activity)
        }

    val density: Density =
        LocalDensity.current

    return remember(windowMetrics, density) {
        val windowDpSize: DpSize =
            with(density) { windowMetrics.bounds.toComposeRect().size.toDpSize() }

        WindowSize(
            width = when {
                windowDpSize.width < 600.dp ->
                    WindowSizeClass.Compact
                windowDpSize.width < 840.dp ->
                    WindowSizeClass.Medium
                else ->
                    WindowSizeClass.Expanded
            },
            height = when {
                windowDpSize.height < 480.dp ->
                    WindowSizeClass.Compact
                windowDpSize.height < 900.dp ->
                    WindowSizeClass.Medium
                else ->
                    WindowSizeClass.Expanded
            },
        )
    }
}
