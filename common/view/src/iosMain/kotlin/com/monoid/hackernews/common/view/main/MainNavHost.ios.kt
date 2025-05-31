package com.monoid.hackernews.common.view.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.ItemIdNavType
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.domain.navigation.UsernameNavType
import com.monoid.hackernews.common.view.comment.CommentDialog
import com.monoid.hackernews.common.view.favorites.FavoriteStoriesListPane
import com.monoid.hackernews.common.view.home.StoriesPane
import com.monoid.hackernews.common.view.itemdetail.ItemDetailPane
import com.monoid.hackernews.common.view.settings.AboutPane
import com.monoid.hackernews.common.view.settings.AppearanceDetailPane
import com.monoid.hackernews.common.view.settings.HelpPane
import com.monoid.hackernews.common.view.settings.NotificationsPane
import com.monoid.hackernews.common.view.settings.SendFeedbackPane
import com.monoid.hackernews.common.view.settings.SettingsListPane
import com.monoid.hackernews.common.view.settings.SettingsViewModel
import com.monoid.hackernews.common.view.settings.TermsOfServicePane
import com.monoid.hackernews.common.view.settings.UserGuidelinesPane
import com.monoid.hackernews.common.view.stories.listContentInsetSides
import org.koin.compose.viewmodel.koinViewModel
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
        startDestination = Route.BottomNav.Stories,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<Route.BottomNav.Stories> {
            val viewModel: HomeViewModel = koinViewModel()
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
            StoriesPane(
                onClickLogin = onClickLogin,
                onClickUser = viewModel::onClickUser,
                onClickStory = viewModel::onClickStory,
                onClickReply = viewModel::onClickReply,
                onClickUrl = onClickUrl,
            )
        }
        composable<Route.BottomNav.Favorites> {
            val viewModel: SettingsViewModel = koinViewModel()
            val lifecycleOwner = LocalLifecycleOwner.current
            LaunchedEffect(Unit) {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    for (event in viewModel.events) {
                        when (event) {
                            is SettingsViewModel.Event.OpenLogin -> {
                                onClickLogin()
                            }

                            is SettingsViewModel.Event.OpenReply -> {
                                navController.navigate(Route.Reply(event.itemId))
                            }
                        }
                    }
                }
            }
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            if (uiState.username.string.isNotBlank()) {
                FavoriteStoriesListPane(
                    username = uiState.username,
                    onClickItem = onClickItem,
                    onClickReply = viewModel::onClickReply,
                    onClickUser = onClickUser,
                    onClickUrl = onClickUrl,
                    onClickLogin = onClickLogin,
                    contentPadding = WindowInsets.safeDrawing
                        .only(listContentInsetSides())
                        .asPaddingValues(),
                )
            } else {
                LaunchedEffect(Unit) {
                    onClickLogin()
                }
            }
        }
        composable<Route.BottomNav.Settings> {
            val viewModel: SettingsViewModel = koinViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            SettingsListPane(
                username = uiState.username,
                onClickLogin = onClickLogin,
                onClickLogout = onClickLogout,
                onClickAppearance = onClickAppearance,
                onClickNotifications = onClickNotifications,
                onClickHelp = onClickHelp,
                onClickTermsOfService = onClickTermsOfService,
                onClickUserGuidelines = onClickUserGuidelines,
                onClickSendFeedback = onClickSendFeedback,
                onClickAbout = onClickAbout,
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
        composable<Route.Settings.Appearance> {
            AppearanceDetailPane()
        }
        composable<Route.Settings.Help> {
            HelpPane()
        }
        composable<Route.Settings.About> {
            AboutPane()
        }
        composable<Route.Settings.SendFeedback> {
            SendFeedbackPane()
        }
        composable<Route.Settings.Notifications> {
            NotificationsPane()
        }
        composable<Route.Settings.TermsOfService> {
            TermsOfServicePane()
        }
        composable<Route.Settings.UserGuidelines> {
            UserGuidelinesPane()
        }
    }
}
