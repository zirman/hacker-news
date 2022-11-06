package com.monoid.hackernews.ui.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.shared.navigation.MainNavigation
import com.monoid.hackernews.ui.settings.Settings

fun NavGraphBuilder.settingsBottomSheet(
    windowSizeClass: WindowSizeClass,
) {
    bottomSheet(
        route = MainNavigation.Settings.route,
        arguments = MainNavigation.Settings.arguments,
    ) {
        Settings(
            windowSizeClass = windowSizeClass,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
        )
    }
}
