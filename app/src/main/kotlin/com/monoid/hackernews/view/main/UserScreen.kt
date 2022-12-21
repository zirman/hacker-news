package com.monoid.hackernews.view.main

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.getSystemService
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.domain.LiveUpdateUseCase
import com.monoid.hackernews.common.navigation.MainNavigation
import com.monoid.hackernews.common.ui.util.itemIdSaver
import com.monoid.hackernews.view.home.HomeScreen

fun NavGraphBuilder.userScreen(
    mainViewModel: MainViewModel,
    context: Context,
    windowSizeClass: WindowSizeClass,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    onNavigateToUser: (Username) -> Unit,
    onNavigateToReply: (ItemId) -> Unit,
    onNavigateToLogin: (LoginAction) -> Unit,
) {
    composable(
        route = MainNavigation.User.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = "http://news.ycombinator.com/user?id={username}" },
            navDeepLink { uriPattern = "https://news.ycombinator.com/user?id={username}" },
        ),
        arguments = MainNavigation.User.arguments,
        enterTransition = MainNavigation.User.enterTransition,
        exitTransition = MainNavigation.User.exitTransition,
        popEnterTransition = MainNavigation.User.popEnterTransition,
        popExitTransition = MainNavigation.User.popExitTransition,
    ) { navBackStackEntry ->
        val username: Username =
            MainNavigation.User.argsFromRoute(navBackStackEntry = navBackStackEntry)

        val (selectedItemId, setSelectedItemId) =
            rememberSaveable(stateSaver = itemIdSaver) { mutableStateOf(null) }

        // Used to keep track of if the story was scrolled last.
        val (detailInteraction, setDetailInteraction) =
            rememberSaveable { mutableStateOf(false) }

        HomeScreen(
            authentication = mainViewModel.authentication,
            itemTreeRepository = mainViewModel.itemTreeRepository,
            drawerState = drawerState,
            windowSizeClass = windowSizeClass,
            title = username.string,
            orderedItemRepo = remember(mainViewModel, username) {
                LiveUpdateUseCase(
                    context.getSystemService()!!,
                    mainViewModel.userStoryRepository,
                )
            },
            snackbarHostState = snackbarHostState,
            selectedItemId = selectedItemId,
            setSelectedItemId = setSelectedItemId,
            detailInteraction = detailInteraction,
            setDetailInteraction = setDetailInteraction,
            onNavigateToUser = onNavigateToUser,
            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin
        )
    }
}
