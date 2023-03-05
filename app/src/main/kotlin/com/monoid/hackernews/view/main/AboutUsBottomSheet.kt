package com.monoid.hackernews.view.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.common.navigation.MainNavigation
import com.monoid.hackernews.view.aboutus.AboutUs

fun NavGraphBuilder.aboutUsBottomSheet(
    windowSizeClassState: State<WindowSizeClass>,
) {
    bottomSheet(
        route = MainNavigation.AboutUs.route,
        arguments = MainNavigation.AboutUs.arguments,
    ) {
        AboutUs(
            windowSizeClass = windowSizeClassState.value,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        )
    }
}
