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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.navigation.MainNavigation
import com.monoid.hackernews.view.login.LoginContent
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch

fun NavGraphBuilder.loginBottomSheet(
    authentication: DataStore<Authentication>,
    itemTreeRepository: ItemTreeRepository,
    httpClient: HttpClient,
    windowSizeClassState: State<WindowSizeClass>,
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

        val coroutineScope = rememberCoroutineScope()

        LoginContent(
            httpClient = httpClient,
            authentication = authentication,
            windowSizeClass = windowSizeClassState.value,
            onLogin = { authentication ->
                when (loginAction) {
                    is LoginAction.Login -> {}
                    is LoginAction.Upvote -> {
                        coroutineScope.launch {
                            itemTreeRepository.upvoteItemJob(
                                authentication,
                                ItemId(loginAction.itemId)
                            )
                        }
                    }
                    is LoginAction.Favorite -> {
                        coroutineScope.launch {
                            itemTreeRepository.favoriteItemJob(
                                authentication,
                                ItemId(loginAction.itemId)
                            )
                        }
                    }
                    is LoginAction.Flag -> {
                        coroutineScope.launch {
                            itemTreeRepository.flagItemJob(
                                authentication,
                                ItemId(loginAction.itemId)
                            )
                        }
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
