package com.monoid.hackernews.common.view.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.ItemIdNavType
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.domain.navigation.UsernameNavType
import com.monoid.hackernews.common.view.comment.CommentDialog
import com.monoid.hackernews.common.view.favorites.FavoriteStoriesListPane
import com.monoid.hackernews.common.view.home.HomeScaffold
import com.monoid.hackernews.common.view.itemdetail.ItemDetailPane
import com.monoid.hackernews.common.view.settings.SettingsListPane
import com.monoid.hackernews.common.view.settings.SettingsViewModel
import com.monoid.hackernews.common.view.stories.listContentPadding
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.typeOf

@Composable
actual fun MainNavHost(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Route.BottomNav.Stories,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<Route.BottomNav.Stories> {
            val viewModel: HomeViewModel = koinViewModel()
            HomeScaffold(
                onClickLogin = onClickLogin,
                onClickLogout = onClickLogout,
                onClickUser = { navController.navigate(Route.User(it)) },
                onClickStory = { navController.navigate(Route.Story(it.id)) },
                onClickReply = {
                    if (viewModel.isLoggedIn) {
                        navController.navigate(Route.Reply(it))
                    } else {
                        onClickLogin()
                    }
                },
                onClickUrl = onClickUrl,
            )
        }
        composable<Route.BottomNav.Favorites> {
            val viewModel: SettingsViewModel = koinViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            if (uiState.username.string.isNotBlank()) {
                val scope = rememberCoroutineScope()
                FavoriteStoriesListPane(
                    username = uiState.username,
                    onClickItem = { item ->
//                        scope.launch {
//                            navigator.navigateTo(
//                                pane = ListDetailPaneScaffoldRole.Detail,
//                                contentKey = "${item.id.long}",
//                            )
//                        }
                    },
                    onClickReply = onClickReply,
                    onClickUser = onClickUser,
                    onClickUrl = onClickUrl,
                    onClickLogin = onClickLogin,
                    contentPadding = listContentPadding(),
                )
            } else {
                LaunchedEffect(Unit) {
                    onClickLogin()
                }
            }
        }
        composable<Route.BottomNav.Settings> {
            val viewModel: SettingsViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val (_, username) = uiState
            SettingsListPane(
                username = username,
                onClickLogin = onClickLogin,
                onClickLogout = onClickLogout,
                onClickAppearance = {
//                    scope.launch {
//                        navigator.navigateTo(
//                            pane = ListDetailPaneScaffoldRole.Detail,
//                            contentKey = SettingsDetailUiState.Appearance.ordinal,
//                        )
//                    }
                },
                onClickNotifications = {
//                    scope.launch {
//                        navigator.navigateTo(
//                            pane = ListDetailPaneScaffoldRole.Detail,
//                            contentKey = SettingsDetailUiState.Notifications.ordinal,
//                        )
//                    }
                },
                onClickHelp = {
//                    scope.launch {
//                        navigator.navigateTo(
//                            pane = ListDetailPaneScaffoldRole.Detail,
//                            contentKey = SettingsDetailUiState.Help.ordinal,
//                        )
//                    }
                },
                onClickTermsOfService = {
//                    scope.launch {
//                        navigator.navigateTo(
//                            pane = ListDetailPaneScaffoldRole.Detail,
//                            contentKey = SettingsDetailUiState.TermsOfService.ordinal,
//                        )
//                    }
                },
                onClickUserGuidelines = {
//                    scope.launch {
//                        navigator.navigateTo(
//                            pane = ListDetailPaneScaffoldRole.Detail,
//                            contentKey = SettingsDetailUiState.UserGuidelines.ordinal,
//                        )
//                    }
                },
                onClickSendFeedback = {
//                    scope.launch {
//                        navigator.navigateTo(
//                            pane = ListDetailPaneScaffoldRole.Detail,
//                            contentKey = SettingsDetailUiState.SendFeedback.ordinal,
//                        )
//                    }
                },
                onClickAbout = {
//                    scope.launch {
//                        navigator.navigateTo(
//                            pane = ListDetailPaneScaffoldRole.Detail,
//                            contentKey = SettingsDetailUiState.About.ordinal,
//                        )
//                    }
                },
            )
        }
        composable<Route.Story>(
            typeMap = mapOf(typeOf<ItemId>() to NavType.ItemIdNavType),
        ) { navBackStackEntry ->
            val itemId = navBackStackEntry.toRoute<Route.Story>().itemId
            ItemDetailPane(
                itemId = itemId,
                onClickUrl = onClickUrl,
                onClickUser = {},
                onClickReply = {},
                onClickLogin = onClickLogin,
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
            )
        }
        composable<Route.User>(
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
