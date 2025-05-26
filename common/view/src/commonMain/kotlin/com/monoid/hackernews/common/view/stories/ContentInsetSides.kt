@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@Composable
fun listContentInsetSides(windowSizeClass: WindowSizeClass = calculateWindowSizeClass()): WindowInsetsSides {
    var windowInsetsSides = WindowInsetsSides.Top + WindowInsetsSides.Start
    windowInsetsSides += if (
        windowSizeClass.heightSizeClass >= WindowHeightSizeClass.Medium &&
        windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Expanded
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
fun detailContentInsetSides(windowSizeClass: WindowSizeClass = calculateWindowSizeClass()): WindowInsetsSides {
    var windowInsetsSides = WindowInsetsSides.Top + WindowInsetsSides.End
    windowInsetsSides += if (
        windowSizeClass.heightSizeClass >= WindowHeightSizeClass.Medium &&
        windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Expanded
    ) {
        // nav buttons are on the side
        WindowInsetsSides.Bottom
    } else {
        // nav buttons are on the bottom except on iOS
        WindowInsetsSides.Start
    }
    return windowInsetsSides
}
