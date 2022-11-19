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
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.data.LoginAction
import com.monoid.hackernews.shared.navigation.MainNavigation
import com.monoid.hackernews.view.login.LoginContent

fun NavGraphBuilder.loginBottomSheet(
    mainViewModel: MainViewModel,
    windowSizeClass: WindowSizeClass,
    onNavigateToReply: (ItemId) -> Unit,
    onNavigateUp: () -> Unit,
    onLoginError: (Throwable) -> Unit,
) {
    bottomSheet(
        route = MainNavigation.Login.route,
        arguments = MainNavigation.Login.arguments,
    ) { navBackStackEntry ->
        val loginAction: LoginAction =
            MainNavigation.Login.argsFromRoute(navBackStackEntry = navBackStackEntry)

        LoginContent(
            windowSizeClass = windowSizeClass,
            onLogin = { authentication ->
                when (loginAction) {
                    is LoginAction.Login -> {}
                    is LoginAction.Upvote -> {
                        mainViewModel.itemTreeRepository.upvoteItemJob(
                            authentication,
                            ItemId(loginAction.itemId)
                        )
                    }
                    is LoginAction.Favorite -> {
                        mainViewModel.itemTreeRepository.favoriteItemJob(
                            authentication,
                            ItemId(loginAction.itemId)
                        )
                    }
                    is LoginAction.Flag -> {
                        mainViewModel.itemTreeRepository.flagItemJob(
                            authentication,
                            ItemId(loginAction.itemId)
                        )
                    }
                    is LoginAction.Reply -> {
                        onNavigateToReply(ItemId(loginAction.itemId))
                    }
                }

                onNavigateUp()
            },
            onLoginError = onLoginError,
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
