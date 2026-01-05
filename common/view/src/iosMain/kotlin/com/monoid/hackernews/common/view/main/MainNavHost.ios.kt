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
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.Route
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
import io.ktor.http.Url

@Composable
fun MainNavDisplay(
    backStack: List<Route>,
    onNavigate: (Route) -> Unit,
    onNavigateUp: () -> Unit,
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
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        onBack = onNavigateUp,
        entryProvider = entryProvider {
            entry<Route.BottomNav.Stories> {
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
                                    onNavigate(Route.Reply(event.itemId))
                                }

                                is HomeViewModel.Event.OpenUser -> {
                                    onNavigate(Route.User(event.username))
                                }

                                is HomeViewModel.Event.OpenStory -> {
                                    onNavigate(Route.Story(event.itemId))
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
            entry<Route.BottomNav.Favorites> {
                val viewModel: SettingsViewModel = metroViewModel()
                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(Unit) {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        for (event in viewModel.events) {
                            when (event) {
                                is SettingsViewModel.Event.OpenLogin -> {
                                    onClickLogin()
                                }

                                is SettingsViewModel.Event.OpenReply -> {
                                    onNavigate(Route.Reply(event.itemId))
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
            entry<Route.BottomNav.Settings> {
                val viewModel: SettingsViewModel = metroViewModel()
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
            entry<Route.Story> { key ->
                val itemId = key.itemId
                val viewModel: SettingsViewModel = metroViewModel()
                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(Unit) {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        for (event in viewModel.events) {
                            when (event) {
                                is SettingsViewModel.Event.OpenLogin -> {
                                    onClickLogin()
                                }

                                is SettingsViewModel.Event.OpenReply -> {
                                    onNavigate(Route.Reply(event.itemId))
                                }
                            }
                        }
                    }
                }
                ItemDetailPane(
                    itemId = itemId,
                    onClickUrl = onClickUrl,
                    onClickUser = onClickUser,
                    onClickReply = viewModel::onClickReply,
                    onClickLogin = onClickLogin,
                )
            }
            entry<Route.User> { key ->
                Text(key.username.string)
            }
            entry<Route.Reply> { navBackStackEntry ->
                CommentDialog(
                    navBackStackEntry.parentId,
                    onDismiss = onNavigateUp,
                    modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()),
                )
            }
            entry<Route.Settings.Appearance> {
                AppearanceDetailPane()
            }
            entry<Route.Settings.Help> {
                HelpPane()
            }
            entry<Route.Settings.About> {
                AboutPane()
            }
            entry<Route.Settings.SendFeedback> {
                SendFeedbackPane()
            }
            entry<Route.Settings.Notifications> {
                NotificationsPane()
            }
            entry<Route.Settings.TermsOfService> {
                TermsOfServicePane()
            }
            entry<Route.Settings.UserGuidelines> {
                UserGuidelinesPane()
            }
        },
        modifier = modifier.fillMaxSize(),
    )
}
