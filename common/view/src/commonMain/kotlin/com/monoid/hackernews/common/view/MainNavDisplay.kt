@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.domain.navigation.BottomNav
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
    entries: List<NavEntry<NavKey>>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
//    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
//    val directive = remember(windowAdaptiveInfo) {
//        calculatePaneScaffoldDirective(windowAdaptiveInfo)
//            .copy(horizontalPartitionSpacerSize = 0.dp)
//    }
//    val listDetailStrategy = rememberListDetailSceneStrategy<Route>(directive = directive)
    NavDisplay(
        entries = entries,
        onBack = onBack,
//        sceneStrategy = listDetailStrategy,
        modifier = modifier,
//        entryDecorators = entryDecorators,
        transitionSpec = {
            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        },
        popTransitionSpec = {
            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
        },
        predictivePopTransitionSpec = {
            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
        },
    )
}

fun NavKey.navEntries(
    navigator: Navigator,
    onClickUrl: (Url) -> Unit,
    onShowLoginDialog: () -> Unit,
): NavEntry<NavKey> {
    return when (this) {
        is BottomNav -> navEntries(
            navigator = navigator,
            onClickUrl = onClickUrl,
            onShowLoginDialog = onShowLoginDialog,
        )

        is Route.Settings -> navEntries()

        is Route.Story -> NavEntry(
            key = this,
//        metadata = ListDetailSceneStrategy.listPane(),
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
                                navigator.navigate(Route.Reply(event.itemId))
                            }
                        }
                    }
                }
            }
            ItemDetailPane(
                itemId = itemId,
                onClickUrl = onClickUrl,
                onClickUser = { navigator.navigate(Route.User(it)) },
                onClickReply = viewModel::onClickReply,
                onClickLogin = onShowLoginDialog,
            )
        }

        is Route.Reply -> NavEntry(
            key = this,
//        metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            CommentDialog(
                parentId = parentId,
                onDismiss = navigator::goBack,
                modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()),
            )
        }

        is Route.User -> NavEntry(
            key = this,
//        metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            Text(username.string)
        }

        else -> {
            error("")
        }
    }
}

private fun BottomNav.navEntries(
    navigator: Navigator,
    onClickUrl: (Url) -> Unit,
    onShowLoginDialog: () -> Unit,
): NavEntry<NavKey> = when (this) {
    is BottomNav.Favorites -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.listPane(),
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
//                            backStack.navigateTo(Route.Reply(event.itemId))
                        }
                    }
                }
            }
        }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        if (uiState.username.string.isNotBlank()) {
            FavoriteStoriesListPane(
                username = uiState.username,
                onClickItem = { navigator.navigate(Route.Story(it.id)) },
                onClickReply = viewModel::onClickReply,
                onClickUser = { navigator.navigate(Route.User(it)) },
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

    is BottomNav.Settings -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.listPane(),
    ) {
        val viewModel: SettingsViewModel = metroViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        SettingsListPane(
            username = uiState.username,
            onClickLogin = onShowLoginDialog,
            onClickLogout = { /* showLogoutDialog = true */ },
            onClickAppearance = { navigator.navigate(Route.Settings.Appearance) },
            onClickNotifications = { navigator.navigate(Route.Settings.Notifications) },
            onClickHelp = { navigator.navigate(Route.Settings.Help) },
            onClickTermsOfService = { navigator.navigate(Route.Settings.TermsOfService) },
            onClickUserGuidelines = { navigator.navigate(Route.Settings.UserGuidelines) },
            onClickSendFeedback = { navigator.navigate(Route.Settings.SendFeedback) },
            onClickAbout = { navigator.navigate(Route.Settings.About) },
        )
    }

    is BottomNav.Stories -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.listPane(),
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
                            navigator.navigate(Route.Reply(event.itemId))
                        }

                        is HomeViewModel.Event.OpenUser -> {
                            navigator.navigate(Route.User(event.username))
                        }

                        is HomeViewModel.Event.OpenStory -> {
                            navigator.navigate(Route.Story(event.itemId))
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

private fun Route.Settings.navEntries(): NavEntry<NavKey> = when (this) {
    is Route.Settings.About -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        AboutPane()
    }

    is Route.Settings.Appearance -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        AppearanceDetailPane()
    }

    is Route.Settings.Help -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        HelpPane()
    }

    is Route.Settings.Notifications -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        NotificationsPane()
    }

    is Route.Settings.SendFeedback -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        SendFeedbackPane()
    }

    is Route.Settings.TermsOfService -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        TermsOfServicePane()
    }

    is Route.Settings.UserGuidelines -> NavEntry(
        key = this,
//        metadata = ListDetailSceneStrategy.detailPane(),
    ) {
        UserGuidelinesPane()
    }
}
