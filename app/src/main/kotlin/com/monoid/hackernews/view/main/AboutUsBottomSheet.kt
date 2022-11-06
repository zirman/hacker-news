package com.monoid.hackernews.view.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.shared.navigation.MainNavigation
import com.monoid.hackernews.view.aboutus.AboutUs

fun NavGraphBuilder.aboutUsBottomSheet(
    windowSizeClass: WindowSizeClass,
) {
    bottomSheet(
        route = MainNavigation.AboutUs.route,
        arguments = MainNavigation.AboutUs.arguments,
    ) {
        AboutUs(
            windowSizeClass = windowSizeClass,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
        )
    }
}
