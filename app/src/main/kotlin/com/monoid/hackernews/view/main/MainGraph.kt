package com.monoid.hackernews.view.main

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.navigation.MainNavigation

val mainGraphRoutePattern = "main"

fun NavGraphBuilder.mainGraph(
    mainViewModel: MainViewModel,
    context: Context,
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
            mainViewModel = mainViewModel,
            context = context,
            windowSizeClass = windowSizeClass,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState,
            onNavigateToUser = onNavigateToUser,
            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin,
        )

        homeScreen(
            mainViewModel = mainViewModel,
            context = context,
            windowSizeClass = windowSizeClass,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState,
            onNavigateToUser = onNavigateToUser,
            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin,
        )

        loginBottomSheet(
            authentication = mainViewModel.authentication,
            itemTreeRepository = mainViewModel.itemTreeRepository,
            httpClient = mainViewModel.httpClient,
            windowSizeClass = windowSizeClass,
            onNavigateToReply = onNavigateToReply,
            onNavigateUp = onNavigateUp,
            onLoginError = onLoginError,
        )

        replyBottomSheet(
            authentication = mainViewModel.authentication,
            itemTreeRepository = mainViewModel.itemTreeRepository,
            httpClient = mainViewModel.httpClient,
            windowSizeClass = windowSizeClass,
            onNavigateUp = onNavigateUp,
            onLoginError = onLoginError,
        )

        aboutUsBottomSheet(
            windowSizeClass = windowSizeClass,
        )

        settingsBottomSheet(
            authentication = mainViewModel.authentication,
            windowSizeClass = windowSizeClass,
        )
    }
}
