package com.monoid.hackernews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.comment.CommentDialog
import com.monoid.hackernews.common.view.currentBottomNav
import com.monoid.hackernews.common.view.favorites.FavoriteStoriesListPane
import com.monoid.hackernews.common.view.itemdetail.ItemDetailPane
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.main.HomeViewModel
import com.monoid.hackernews.common.view.navigateTo
import com.monoid.hackernews.common.view.navigateUp
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
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import com.monoid.hackernews.common.view.stories.listContentInsetSides
import com.monoid.hackernews.common.view.theme.AppTheme
import io.ktor.http.Url
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor

@Composable
fun DesktopApp(onClickUrl: (Url) -> Unit) {
    AppTheme {
        var showLoginDialog by rememberSaveable {
            mutableStateOf(false)
        }
        if (showLoginDialog) {
            LoginDialog(onDismissRequest = { showLoginDialog = false })
        }
        val backStack: SnapshotStateList<Route> =
            rememberSerializable(serializer = SnapshotStateListSerializer()) {
                mutableStateListOf(Route.BottomNav.Stories)
            }
        Scaffold(
            bottomBar = {
                val currentBottomNavDestination: Route.BottomNav? =
                    backStack.currentBottomNav()
                BottomAppBar(
                    actions = {
//                        Route.BottomNav.entries.forEach { destination ->
//                            IconButton(
//                                onClick = {
//                                    val range = backStack.currentStack(destination.instance)
//                                    val bottomNavStack = backStack.slice(range)
//                                    backStack.removeRange(
//                                        fromIndex = range.first,
//                                        toIndex = range.last + 1,
//                                    )
//                                    backStack.addAll(bottomNavStack)
//                                    if (backStack.first() != Route.BottomNav.Stories) {
//                                        backStack.add(0, Route.BottomNav.Stories)
//                                    }
//                                },
//                            ) {
//                                Icon(
//                                    imageVector = if (destination == currentBottomNavDestination) {
//                                        destination.selectedIcon
//                                    } else {
//                                        destination.icon
//                                    },
//                                    contentDescription = stringResource(destination.contentDescription),
//                                )
//                            }
//                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* do something */ },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        ) {
                            Icon(Icons.Filled.Add, "Localized description")
                        }
                    },
                )
            },
        ) { innerPadding ->
            NavDisplay(
                backStack = backStack,
                modifier = Modifier.padding(innerPadding),
                entryProvider = entryProvider {
//                    entry<Route.BottomNav.Stories> {
//                        HNPanes(
//                            onClickLogin = { showLoginDialog = true },
//                            onClickUrl = onClickUrl,
//                        )
//                    }
                    entry<Route.BottomNav.Stories> {
                        val viewModel: HomeViewModel = metroViewModel()
                        val lifecycleOwner = LocalLifecycleOwner.current
                        LaunchedEffect(Unit) {
                            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                for (event in viewModel.events) {
                                    when (event) {
                                        is HomeViewModel.Event.OpenLogin -> {
                                            showLoginDialog = true
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
                            onClickLogin = { showLoginDialog = true },
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
                                            showLoginDialog = true
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
                                onClickLogin = { showLoginDialog = true },
                                contentPadding = WindowInsets.safeDrawing
                                    .only(listContentInsetSides())
                                    .asPaddingValues(),
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                showLoginDialog = true
                            }
                        }
                    }
                    entry<Route.BottomNav.Settings> {
                        val viewModel: SettingsViewModel = metroViewModel()
                        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                        SettingsListPane(
                            username = uiState.username,
                            onClickLogin = { showLoginDialog = true },
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
                    entry<Route.Story> { key ->
                        val itemId = key.itemId
                        val viewModel: SettingsViewModel = metroViewModel()
                        val lifecycleOwner = LocalLifecycleOwner.current
                        LaunchedEffect(Unit) {
                            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                for (event in viewModel.events) {
                                    when (event) {
                                        is SettingsViewModel.Event.OpenLogin -> {
                                            showLoginDialog = true
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
                            onClickLogin = { showLoginDialog = true },
                        )
                    }
                    entry<Route.User> { key ->
                        Text(key.username.string)
                    }
                    entry<Route.Reply> { navBackStackEntry ->
                        CommentDialog(
                            navBackStackEntry.parentId,
                            onDismiss = backStack::navigateUp,
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
                }
            )
        }
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun HNPanes(
    onClickLogin: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    val splitterState = rememberSplitPaneState()
    var itemId by rememberSaveable {
        mutableLongStateOf(-1L)
    }
    HorizontalSplitPane(
        splitPaneState = splitterState,
        modifier = modifier,
    ) {
        first(640.dp) {
            val viewModel: StoriesViewModel = metroViewModel(
                extras = StoriesViewModel.extras(StoryOrdering.Trending),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            ItemsColumn(
                itemsList = uiState.itemsList,
                onVisibleItem = viewModel::updateItem,
                onClickItem = {
                    itemId = it.id.long
                },
                onClickReply = {},
                onClickUser = {},
                onClickUrl = onClickUrl,
                onClickUpvote = {},
                onClickFavorite = {},
                onClickFollow = {},
                onClickFlag = {},
                contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
                modifier = Modifier.fillMaxHeight(),
            ) {}
        }
        second(640.dp) {
            if (itemId != -1L) {
                ItemDetailPane(
                    itemId = ItemId(itemId),
                    onClickUrl = onClickUrl,
                    onClickUser = {},
                    onClickReply = {},
                    onClickLogin = onClickLogin,
                )
            }
        }
        splitter {
            visiblePart {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background),
                )
            }
            handle {
                Box(
                    Modifier
                        .markAsHandle()
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                        .background(SolidColor(Color.Gray), alpha = 0.50f)
                        .width(9.dp)
                        .fillMaxHeight(),
                )
            }
        }
    }
}
