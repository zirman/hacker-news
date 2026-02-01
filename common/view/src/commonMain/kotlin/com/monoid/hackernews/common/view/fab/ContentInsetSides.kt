package com.monoid.hackernews.common.view.fab

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

@Composable
fun listContentInsetSides(windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass): WindowInsetsSides {
    var windowInsetsSides = WindowInsetsSides.Top + WindowInsetsSides.Start
    windowInsetsSides += if (
        windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) &&
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    ) {
        // nav buttons are on the side
        WindowInsetsSides.Bottom
    } else {
        // nav buttons are on the bottom except on iOS
        WindowInsetsSides.End
    }
    return windowInsetsSides
}

@Composable
fun detailContentInsetSides(windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass): WindowInsetsSides {
    var windowInsetsSides = WindowInsetsSides.Top + WindowInsetsSides.End
    windowInsetsSides += if (
        windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) &&
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    ) {
        // nav buttons are on the side
        WindowInsetsSides.Bottom
    } else {
        // nav buttons are on the bottom except on iOS
        WindowInsetsSides.Start
    }
    return windowInsetsSides
}
