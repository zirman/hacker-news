@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.comment.CommentDialog
import com.monoid.hackernews.common.view.fab.listContentInsetSides
import com.monoid.hackernews.common.view.favorites.FavoriteStoriesListPane
import com.monoid.hackernews.common.view.itemdetail.ItemDetailPane
import com.monoid.hackernews.common.view.main.HomeViewModel
import com.monoid.hackernews.common.view.settings.AboutPane
import com.monoid.hackernews.common.view.settings.AppearanceDetailPane
import com.monoid.hackernews.common.view.settings.HelpPane
import com.monoid.hackernews.common.view.settings.NotificationsPane
import com.monoid.hackernews.common.view.settings.SendFeedbackPane
import com.monoid.hackernews.common.view.settings.SettingsListPane
import com.monoid.hackernews.common.view.settings.SettingsViewModel
import com.monoid.hackernews.common.view.settings.TermsOfServicePane
import com.monoid.hackernews.common.view.settings.UserGuidelinesPane
import com.monoid.hackernews.common.view.stories.StoriesPane
import io.ktor.http.Url

@Composable
fun MainNavDisplay(
    backStack: SnapshotStateList<Route>,
    onClickUrl: (Url) -> Unit,
    onShowLoginDialog: () -> Unit,
    modifier: Modifier = Modifier,
    entryDecorators: List<NavEntryDecorator<Route>> = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
    ),
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<Route>(directive = directive)
    NavDisplay(
        backStack = backStack,
        sceneStrategy = listDetailStrategy,
        modifier = modifier,
        entryProvider = { key ->
            key.navEntries(
                backStack = backStack,
                onClickUrl = onClickUrl,
                onShowLoginDialog = onShowLoginDialog,
            )
        },
        entryDecorators = entryDecorators,
    )
}

private fun Route.navEntries(
    backStack: SnapshotStateList<Route>,
    onClickUrl: (Url) -> Unit,
    onShowLoginDialog: () -> Unit,
): NavEntry<Route> = when (this) {

    is Route.BottomNav -> navEntries(
        backStack = backStack,
        onClickUrl = onClickUrl,
        onShowLoginDialog = onShowLoginDialog,
    )

    is Route.Settings -> navEntries()

    is Route.Story -> NavEntry(
        this,
        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        val viewModel: SettingsViewModel = metroViewModel()
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(Unit) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                for (event in viewModel.events) {
                    when (event) {
                        is SettingsViewModel.Event.OpenLogin -> {
                            onShowLoginDialog()
                        }

                        is SettingsViewModel.Event.OpenReply -> {
                            backStack.navigateTo(Route.Reply(event.itemId))
                        }
                    }
                }
            }
        }
        ItemDetailPane(
            itemId = itemId,
            onClickUrl = onClickUrl,
            onClickUser = { backStack.navigateTo(Route.User(it)) },
            onClickReply = viewModel::onClickReply,
            onClickLogin = onShowLoginDialog,
        )
    }

    is Route.Reply -> NavEntry(this) {
        CommentDialog(
            parentId = parentId,
            onDismiss = backStack::navigateUp,
            modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()),
        )
    }

    is Route.User -> NavEntry(this) {
        Text(username.string)
    }
}

private fun Route.BottomNav.navEntries(
    backStack: SnapshotStateList<Route>,
    onClickUrl: (Url) -> Unit,
    onShowLoginDialog: () -> Unit,
): NavEntry<Route> = when (this) {
    Route.BottomNav.Favorites -> NavEntry(this) {
        val viewModel: SettingsViewModel = metroViewModel()
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(Unit) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                for (event in viewModel.events) {
                    when (event) {
                        is SettingsViewModel.Event.OpenLogin -> {
                            onShowLoginDialog()
                        }

                        is SettingsViewModel.Event.OpenReply -> {
                            backStack.navigateTo(Route.Reply(event.itemId))
                        }
                    }
                }
            }
        }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        if (uiState.username.string.isNotBlank()) {
            FavoriteStoriesListPane(
                username = uiState.username,
                onClickItem = { backStack.navigateTo(Route.Story(it.id)) },
                onClickReply = viewModel::onClickReply,
                onClickUser = { backStack.navigateTo(Route.User(it)) },
                onClickUrl = onClickUrl,
                onClickLogin = onShowLoginDialog,
                contentPadding = WindowInsets.safeDrawing
                    .only(listContentInsetSides())
                    .asPaddingValues(),
            )
        } else {
            LaunchedEffect(Unit) {
                onShowLoginDialog()
            }
        }
    }

    Route.BottomNav.Settings -> NavEntry(this) {
        val viewModel: SettingsViewModel = metroViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        SettingsListPane(
            username = uiState.username,
            onClickLogin = onShowLoginDialog,
            onClickLogout = { /* showLogoutDialog = true */ },
            onClickAppearance = { backStack.navigateTo(Route.Settings.Appearance) },
            onClickNotifications = { backStack.navigateTo(Route.Settings.Notifications) },
            onClickHelp = { backStack.navigateTo(Route.Settings.Help) },
            onClickTermsOfService = { backStack.navigateTo(Route.Settings.TermsOfService) },
            onClickUserGuidelines = { backStack.navigateTo(Route.Settings.UserGuidelines) },
            onClickSendFeedback = { backStack.navigateTo(Route.Settings.SendFeedback) },
            onClickAbout = { backStack.navigateTo(Route.Settings.About) },
        )
    }

    Route.BottomNav.Stories -> NavEntry(
        this,
        metadata = ListDetailSceneStrategy.listPane(),
    ) {
        val viewModel: HomeViewModel = metroViewModel()
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(Unit) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                for (event in viewModel.events) {
                    when (event) {
                        is HomeViewModel.Event.OpenLogin -> {
                            onShowLoginDialog()
                        }

                        is HomeViewModel.Event.OpenReply -> {
                            backStack.navigateTo(Route.Reply(event.itemId))
                        }

                        is HomeViewModel.Event.OpenUser -> {
                            backStack.navigateTo(Route.User(event.username))
                        }

                        is HomeViewModel.Event.OpenStory -> {
                            backStack.navigateTo(Route.Story(event.itemId))
                        }
                    }
                }
            }
        }
        StoriesPane(
            onClickLogin = onShowLoginDialog,
            onClickUser = viewModel::onClickUser,
            onClickStory = viewModel::onClickStory,
            onClickReply = viewModel::onClickReply,
            onClickUrl = onClickUrl,
        )
    }
}

private fun Route.Settings.navEntries(): NavEntry<Route> = when (this) {
    is Route.Settings.About -> NavEntry(this) {
        AboutPane()
    }

    is Route.Settings.Appearance -> NavEntry(this) {
        AppearanceDetailPane()
    }

    is Route.Settings.Help -> NavEntry(this) {
        HelpPane()
    }

    is Route.Settings.Notifications -> NavEntry(this) {
        NotificationsPane()
    }

    is Route.Settings.SendFeedback -> NavEntry(this) {
        SendFeedbackPane()
    }

    is Route.Settings.TermsOfService -> NavEntry(this) {
        TermsOfServicePane()
    }

    is Route.Settings.UserGuidelines -> NavEntry(this) {
        UserGuidelinesPane()
    }
}
