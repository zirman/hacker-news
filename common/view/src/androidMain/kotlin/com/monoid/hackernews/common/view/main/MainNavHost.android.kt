package com.monoid.hackernews.common.view.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.monoid.hackernews.common.view.metroViewModel
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.ItemIdNavType
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.domain.navigation.UsernameNavType
import com.monoid.hackernews.common.view.comment.CommentDialog
import com.monoid.hackernews.common.view.home.HomeScaffold
import io.ktor.http.Url
import kotlin.reflect.typeOf

@Composable
actual fun MainNavHost(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickItem: (Item) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    onClickAppearance: () -> Unit,
    onClickNotifications: () -> Unit,
    onClickHelp: () -> Unit,
    onClickTermsOfService: () -> Unit,
    onClickUserGuidelines: () -> Unit,
    onClickSendFeedback: () -> Unit,
    onClickAbout: () -> Unit,
    modifier: Modifier,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<Route.Home> {
            val viewModel: HomeViewModel = metroViewModel()
            val lifecycleOwner = LocalLifecycleOwner.current
            LaunchedEffect(Unit) {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    for (event in viewModel.events) {
                        when (event) {
                            is HomeViewModel.Event.OpenLogin -> {
                                onClickLogin()
                            }

                            is HomeViewModel.Event.OpenReply -> {
                                navController.navigate(Route.Reply(event.itemId))
                            }

                            is HomeViewModel.Event.OpenUser -> {
                                navController.navigate(Route.User(event.username))
                            }

                            is HomeViewModel.Event.OpenStory -> {
                                navController.navigate(Route.Story(event.itemId))
                            }
                        }
                    }
                }
            }
            HomeScaffold(
                onClickLogin = onClickLogin,
                onClickLogout = onClickLogout,
                onClickUser = viewModel::onClickUser,
                onClickReply = viewModel::onClickReply,
                onClickUrl = onClickUrl,
            )
        }
        dialog<Route.User>(
            typeMap = mapOf(typeOf<Username>() to NavType.UsernameNavType),
        ) { navBackStackEntry ->
            Text(navBackStackEntry.toRoute<Route.User>().username.string)
        }
        dialog<Route.Reply>(
            typeMap = mapOf(typeOf<ItemId>() to NavType.ItemIdNavType),
        ) { navBackStackEntry ->
            CommentDialog(
                navBackStackEntry.toRoute<Route.Reply>().parentId,
                onDismiss = navController::navigateUp,
                modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()),
            )
        }
    }
}
