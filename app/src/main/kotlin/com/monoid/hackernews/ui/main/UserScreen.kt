package com.monoid.hackernews.ui.main

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
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.data.UserStoryRepository
import com.monoid.hackernews.shared.domain.LiveUpdateUseCase
import com.monoid.hackernews.shared.navigation.LoginAction
import com.monoid.hackernews.shared.navigation.MainNavigation
import com.monoid.hackernews.shared.navigation.Username
import com.monoid.hackernews.shared.ui.util.itemIdSaver
import com.monoid.hackernews.ui.home.HomeScreen

fun NavGraphBuilder.userScreen(
    context: Context,
    windowSizeClass: WindowSizeClass,
    mainViewModel: MainViewModel,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    navigateToUser: (Username) -> Unit,
    navigateToReply: (ItemId) -> Unit,
    navigateToLogin: (LoginAction) -> Unit,
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
            mainViewModel = mainViewModel,
            drawerState = drawerState,
            windowSizeClass = windowSizeClass,
            title = username.string,
            orderedItemRepo = remember(mainViewModel, username) {
                LiveUpdateUseCase(
                    context.getSystemService()!!,
                    UserStoryRepository(
                        httpClient = mainViewModel.httpClient,
                        userDao = mainViewModel.userDao,
                        itemDao = mainViewModel.itemDao,
                        username = username,
                    )
                )
            },
            snackbarHostState = snackbarHostState,
            selectedItemId = selectedItemId,
            setSelectedItemId = setSelectedItemId,
            detailInteraction = detailInteraction,
            setDetailInteraction = setDetailInteraction,
            navigateToUser = navigateToUser,
            navigateToReply = navigateToReply,
            navigateToLogin = navigateToLogin
        )
    }
}
