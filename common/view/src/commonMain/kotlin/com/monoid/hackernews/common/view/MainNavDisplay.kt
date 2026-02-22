@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.monoid.hackernews.common.view.stories.LocalPlatformContext
import com.monoid.hackernews.common.view.stories.StoriesListPane
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import com.monoid.hackernews.common.view.stories.displayMessage
import io.ktor.http.Url
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainNavDisplay(
    entries: List<NavEntry<NavKey>>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val directive: PaneScaffoldDirective = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)
    NavDisplay(
        entries = entries,
        onBack = onBack,
        sceneStrategy = listDetailStrategy,
        modifier = modifier,
        transitionSpec = {
            slideInHorizontally { it / 2 } togetherWith slideOutHorizontally { -it / 2 }
        },
        popTransitionSpec = {
            slideInHorizontally { -it / 2 } togetherWith slideOutHorizontally { it / 2 }
        },
        predictivePopTransitionSpec = {
            slideInHorizontally { -it / 2 } togetherWith slideOutHorizontally { it / 2 }
        },
    )
}

fun NavKey.navEntries(
    navigator: Navigator,
    onClickUrl: (Url) -> Unit,
    onShowLoginDialog: () -> Unit,
    onShowLogoutDialog: () -> Unit,
    contentPadding: State<PaddingValues>,
): NavEntry<NavKey> {
    return when (this) {
        is BottomNav -> navEntries(
            navigator = navigator,
            onClickUrl = onClickUrl,
            onShowLoginDialog = onShowLoginDialog,
            onShowLogoutDialog = onShowLogoutDialog,
            contentPadding = contentPadding,
        )

        is Route.Settings -> navEntries(contentPadding = contentPadding)

        is Route.Story -> NavEntry(
            key = this,
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
                contentPadding = contentPadding.value,
            )
        }

        is Route.Reply -> NavEntry(
            key = this,
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            CommentDialog(
                parentId = parentId,
                onDismiss = navigator::goBack,
                contentPadding = contentPadding.value,
            )
        }

        is Route.User -> NavEntry(
            key = this,
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            Text(
                text = username.string,
                modifier = Modifier.padding(contentPadding.value),
            )
        }

        else -> {
            error("Invalid navEntry")
        }
    }
}

private fun BottomNav.navEntries(
    navigator: Navigator,
    onClickUrl: (Url) -> Unit,
    onShowLoginDialog: () -> Unit,
    onShowLogoutDialog: () -> Unit,
    contentPadding: State<PaddingValues>,
): NavEntry<NavKey> = when (this) {
    is BottomNav.Favorites -> NavEntry(
        key = this,
        metadata = ListDetailSceneStrategy.listPane(
            detailPlaceholder = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(Res.string.no_story_selected),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            },
        ),
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
                            // backStack.navigateTo(Route.Reply(event.itemId))
                        }
                    }
                }
            }
        }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        if (uiState.username.string.isNotBlank()) {
            FavoriteStoriesListPane(
                username = uiState.username,
                onClickItem = { navigator.navigate(Route.Story(it.id)) },
                onClickReply = viewModel::onClickReply,
                onClickUser = { navigator.navigate(Route.User(it)) },
                onClickUrl = onClickUrl,
                onClickLogin = onShowLoginDialog,
                contentPadding = contentPadding.value,
            )
        } else {
            LaunchedEffect(Unit) {
                onShowLoginDialog()
            }
        }
    }

    is BottomNav.Settings -> NavEntry(
        key = this,
        metadata = ListDetailSceneStrategy.listPane(
            detailPlaceholder = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(Res.string.no_setting_selected),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            },
        ),
    ) {
        val viewModel: SettingsViewModel = metroViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        SettingsListPane(
            username = uiState.username,
            onClickLogin = onShowLoginDialog,
            onClickLogout = onShowLogoutDialog,
            onClickAppearance = { navigator.navigate(Route.Settings.Appearance) },
            onClickNotifications = { navigator.navigate(Route.Settings.Notifications) },
            onClickHelp = { navigator.navigate(Route.Settings.Help) },
            onClickTermsOfService = { navigator.navigate(Route.Settings.TermsOfService) },
            onClickUserGuidelines = { navigator.navigate(Route.Settings.UserGuidelines) },
            onClickSendFeedback = { navigator.navigate(Route.Settings.SendFeedback) },
            onClickAbout = { navigator.navigate(Route.Settings.About) },
            contentPadding = contentPadding.value,
        )
    }

    is BottomNav.Stories -> NavEntry(
        key = this,
        metadata = ListDetailSceneStrategy.listPane(
            detailPlaceholder = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(Res.string.no_story_selected),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            },
        ),
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
        val storiesViewModel: StoriesViewModel = metroViewModel(
            extras = StoriesViewModel.extras(StoryOrdering.Trending),
        )
        val platformContext = LocalPlatformContext.current
        LaunchedEffect(Unit) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                for (event in storiesViewModel.events) {
                    when (event) {
                        is StoriesViewModel.Event.Error -> {
                            platformContext.displayMessage(
                                getString(
                                    Res.string.an_error_occurred_format,
                                    event.message.orEmpty(),
                                ),
                            )
                        }

                        is StoriesViewModel.Event.OpenLogin -> {
                            onShowLoginDialog()
                        }
                    }
                }
            }
        }
        StoriesListPane(
            onClickItem = viewModel::onClickStory,
            onClickReply = viewModel::onClickReply,
            onClickUser = viewModel::onClickUser,
            onClickUrl = onClickUrl,
            onClickLogin = onShowLoginDialog,
            contentPadding = contentPadding.value,
        )
    }
}

private fun Route.Settings.navEntries(contentPadding: State<PaddingValues>): NavEntry<NavKey> =
    when (this) {
        is Route.Settings.About -> NavEntry(
            key = this,
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            AboutPane(contentPadding = contentPadding.value)
        }

        is Route.Settings.Appearance -> NavEntry(
            key = this,
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            AppearanceDetailPane(contentPadding = contentPadding.value)
        }

        is Route.Settings.Help -> NavEntry(
            key = this,
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            HelpPane(contentPadding = contentPadding.value)
        }

        is Route.Settings.Notifications -> NavEntry(
            key = this,
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            NotificationsPane(contentPadding = contentPadding.value)
        }

        is Route.Settings.SendFeedback -> NavEntry(
            key = this,
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            SendFeedbackPane(contentPadding = contentPadding.value)
        }

        is Route.Settings.TermsOfService -> NavEntry(
            key = this,
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            TermsOfServicePane(contentPadding = contentPadding.value)
        }

        is Route.Settings.UserGuidelines -> NavEntry(
            key = this,
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            UserGuidelinesPane(contentPadding = contentPadding.value)
        }
    }
