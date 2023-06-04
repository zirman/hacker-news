package com.monoid.hackernews.view.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.getSystemService
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.domain.LiveUpdateUseCase
import com.monoid.hackernews.common.navigation.MainNavigation
import com.monoid.hackernews.common.ui.util.itemIdSaver
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.view.home.HomeScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun NavGraphBuilder.userScreen(
    mainViewModel: MainViewModel,
    context: Context,
    windowSizeClassState: State<WindowSizeClass>,
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
            navDeepLink { uriPattern = "https://news.ycombinator.com/user?id={username}" }
        ),
        arguments = MainNavigation.User.arguments,
        enterTransition = MainNavigation.User.enterTransition,
        exitTransition = MainNavigation.User.exitTransition,
        popEnterTransition = MainNavigation.User.popEnterTransition,
        popExitTransition = MainNavigation.User.popExitTransition
    ) { navBackStackEntry ->
        val username: Username =
            MainNavigation.User.argsFromRoute(navBackStackEntry = navBackStackEntry)

        val (selectedItemId, setSelectedItemId) =
            rememberSaveable(stateSaver = itemIdSaver) { mutableStateOf(null) }

        // Used to keep track of if the story was scrolled last.
        val (detailInteraction, setDetailInteraction) =
            rememberSaveable { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()

        HomeScreen(
            itemTreeRepository = mainViewModel.itemTreeRepository,
            drawerState = drawerState,
            windowSizeClass = windowSizeClassState.value,
            title = username.string,
            orderedItemRepo = remember(mainViewModel, username) {
                LiveUpdateUseCase(
                    context.getSystemService()!!,
                    mainViewModel.userStoryRepositoryFactory.repository(username = username)
                )
            },
            snackbarHostState = snackbarHostState,
            selectedItemId = selectedItemId,
            setSelectedItemId = setSelectedItemId,
            detailInteraction = detailInteraction,
            setDetailInteraction = setDetailInteraction,
            onNavigateToLogin = onNavigateToLogin,
            onClickUser = { user ->
                if (user != null) {
                    onNavigateToUser(user)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.url_is_null),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onClickReply = { itemId ->
                coroutineScope.launch {
                    val auth = mainViewModel.authentication.data.first()

                    if (auth.password.isNotEmpty()) {
                        onNavigateToReply(itemId)
                    } else {
                        onNavigateToLogin(LoginAction.Reply(itemId.long))
                    }
                }
            },
            onClickBrowser = { url ->
                val uri = url?.let { Uri.parse(it) }

                if (uri != null) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.url_is_null),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}
