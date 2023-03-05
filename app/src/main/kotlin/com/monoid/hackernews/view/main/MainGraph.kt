package com.monoid.hackernews.view.main

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.State
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
    windowSizeClassState: State<WindowSizeClass>,
    drawerState: DrawerState,
    onNavigateToUser: (Username) -> Unit,
    onNavigateToReply: (ItemId) -> Unit,
    onNavigateToLogin: (LoginAction) -> Unit,
    onNavigateUp: () -> Unit,
    onLoginError: (Throwable) -> Unit,
) {
    navigation(
        startDestination = MainNavigation.Home.route,
        route = mainGraphRoutePattern
    ) {
        userScreen(
            mainViewModel = mainViewModel,
            context = context,
            windowSizeClassState = windowSizeClassState,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState,
            onNavigateToUser = onNavigateToUser,
            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin
        )

        homeScreen(
            mainViewModel = mainViewModel,
            context = context,
            windowSizeClassState = windowSizeClassState,
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
            windowSizeClassState = windowSizeClassState,
            onNavigateToReply = onNavigateToReply,
            onNavigateUp = onNavigateUp,
            onLoginError = onLoginError
        )

        replyBottomSheet(
            authentication = mainViewModel.authentication,
            itemTreeRepository = mainViewModel.itemTreeRepository,
            httpClient = mainViewModel.httpClient,
            windowSizeClassState = windowSizeClassState,
            onNavigateUp = onNavigateUp,
            onLoginError = onLoginError
        )

        aboutUsBottomSheet(
            windowSizeClassState = windowSizeClassState
        )

        settingsBottomSheet(
            authentication = mainViewModel.authentication,
            windowSizeClassState = windowSizeClassState
        )
    }
}
