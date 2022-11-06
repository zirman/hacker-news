package com.monoid.hackernews.ui.main

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.navigation.LoginAction
import com.monoid.hackernews.shared.navigation.MainNavigation
import com.monoid.hackernews.shared.navigation.Username

val mainGraphRoutePattern = "main"

fun NavGraphBuilder.mainGraph(
    context: Context,
    mainViewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
    windowSizeClass: WindowSizeClass,
    drawerState: DrawerState,
    onNavigateToUser: (Username) -> Unit,
    onNavigateToReply: (ItemId) -> Unit,
    onNavigateToLogin: (LoginAction) -> Unit,
    onNavigateUp: () -> Unit,
    onLoginError: (Throwable) -> Unit,
) {
    navigation(startDestination = MainNavigation.Home.route, route = mainGraphRoutePattern) {
        userScreen(
            context = context,
            windowSizeClass = windowSizeClass,
            mainViewModel = mainViewModel,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState,
            onNavigateToUser = onNavigateToUser,
            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin,
        )

        homeScreen(
            context = context,
            windowSizeClass = windowSizeClass,
            mainViewModel = mainViewModel,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState,
            onNavigateToUser = onNavigateToUser,
            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin,
        )

        loginBottomSheet(
            mainViewModel = mainViewModel,
            windowSizeClass = windowSizeClass,
            onNavigateToReply = onNavigateToReply,
            onNavigateUp = onNavigateUp,
            onLoginError = onLoginError,
        )

        replyBottomSheet(
            windowSizeClass = windowSizeClass,
            onNavigateUp = onNavigateUp,
            onLoginError = onLoginError,
        )

        aboutUsBottomSheet(
            windowSizeClass = windowSizeClass,
        )

        settingsBottomSheet(
            windowSizeClass = windowSizeClass,
        )
    }
}
