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
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.shared.datastore.Authentication
import com.monoid.hackernews.shared.navigation.MainNavigation
import com.monoid.hackernews.view.settings.Settings

fun NavGraphBuilder.settingsBottomSheet(
    authentication: DataStore<Authentication>,
    windowSizeClass: WindowSizeClass,
) {
    bottomSheet(
        route = MainNavigation.Settings.route,
        arguments = MainNavigation.Settings.arguments,
    ) {
        Settings(
            authentication = authentication,
            windowSizeClass = windowSizeClass,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
                .windowInsetsPadding(
                    WindowInsets.safeDrawing
                        .only(WindowInsetsSides.Horizontal)
                ),
        )
    }
}
