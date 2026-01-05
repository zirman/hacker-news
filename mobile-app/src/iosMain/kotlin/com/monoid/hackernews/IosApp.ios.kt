package com.monoid.hackernews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import com.monoid.hackernews.common.core.metro.LocalViewModelProviderFactory
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.Scrim
import com.monoid.hackernews.common.view.home.contentDescription
import com.monoid.hackernews.common.view.home.icon
import com.monoid.hackernews.common.view.home.label
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.logout.LogoutDialog
import com.monoid.hackernews.common.view.main.MainNavDisplay
import com.monoid.hackernews.common.view.theme.AppTheme
import dev.zacsweers.metro.createGraph
import io.ktor.http.Url
import org.jetbrains.compose.resources.stringResource

@Composable
fun IosApp(onClickUrl: (Url) -> Unit) {
    val appGraph = retain { createGraph<IosAppGraph>() }
    CompositionLocalProvider(LocalViewModelProviderFactory provides appGraph.iosViewModelFactory) {
        AppTheme {
            Scrim {
                Box(contentAlignment = Alignment.Center) {
                    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
                    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
                    val backStacks: SnapshotStateList<List<Route>> =
                        rememberSerializable(serializer = SnapshotStateListSerializer()) {
                            mutableStateListOf(
                                listOf(Route.BottomNav.Stories),
                            )
                        }

                    fun navigateTo(route: Route) {
                        backStacks[backStacks.size - 1] = backStacks.last() + route
                    }

                    fun navigateUp() {
                        val backStack = backStacks.last()
                        if (backStack.size <= 1) {
                            backStacks.removeLast()
                        } else {
                            backStacks[backStacks.size - 1] = backStack.dropLast(1)
                        }
                    }
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                Route.BottomNav.entries.forEach { destination ->
                                    NavigationBarItem(
                                        selected = destination == backStacks.last().first(),
                                        onClick = {
                                            val i = backStacks
                                                .indexOfLast { it.firstOrNull() == destination }
                                            val stack = if (i != -1) {
                                                backStacks.removeAt(i)
                                            } else {
                                                listOf(destination)
                                            }
                                            backStacks.add(stack)
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = destination.icon,
                                                contentDescription = stringResource(destination.contentDescription),
                                            )
                                        },
                                        label = { Text(stringResource(destination.label)) },
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        val dir = LocalLayoutDirection.current
                        MainNavDisplay(
                            backStack = backStacks.last(),
                            onNavigate = ::navigateTo,
                            onNavigateUp = ::navigateUp,
                            onClickLogin = {
                                showLoginDialog = true
                            },
                            onClickLogout = {
                                showLogoutDialog = true
                            },
                            onClickItem = {
                                navigateTo(Route.Story(it.id))
                            },
                            onClickReply = { navigateTo(Route.Reply(it)) },
                            onClickUser = { navigateTo(Route.User(it)) },
                            onClickUrl = onClickUrl,
                            onClickAppearance = { navigateTo(Route.Settings.Appearance) },
                            onClickNotifications = { navigateTo(Route.Settings.Notifications) },
                            onClickHelp = { navigateTo(Route.Settings.Help) },
                            onClickTermsOfService = { navigateTo(Route.Settings.TermsOfService) },
                            onClickUserGuidelines = { navigateTo(Route.Settings.UserGuidelines) },
                            onClickSendFeedback = { navigateTo(Route.Settings.SendFeedback) },
                            onClickAbout = { navigateTo(Route.Settings.About) },
                            modifier = Modifier.padding(
                                PaddingValues(
                                    start = padding.calculateStartPadding(dir),
                                    end = padding.calculateEndPadding(dir),
                                    bottom = padding.calculateBottomPadding(),
                                ),
                            ),
                        )
                    }
                    if (showLoginDialog) {
                        LoginDialog(
                            onDismissRequest = {
                                showLoginDialog = false
                            },
                        )
                    }
                    if (showLogoutDialog) {
                        LogoutDialog(
                            onDismissRequest = {
                                showLogoutDialog = false
                            },
                        )
                    }
                }
            }
        }
    }
}
