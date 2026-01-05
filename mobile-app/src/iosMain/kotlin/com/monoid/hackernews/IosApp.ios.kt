@file:OptIn(ExperimentalNativeApi::class)

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
import kotlin.experimental.ExperimentalNativeApi

@Composable
fun IosApp(onClickUrl: (Url) -> Unit) {
    val appGraph = retain { createGraph<IosAppGraph>() }
    CompositionLocalProvider(LocalViewModelProviderFactory provides appGraph.iosViewModelFactory) {
        AppTheme {
            Scrim {
                Box(contentAlignment = Alignment.Center) {
                    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
                    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
                    val backStack: SnapshotStateList<Route> =
                        rememberSerializable(serializer = SnapshotStateListSerializer()) {
                            mutableStateListOf(Route.BottomNav.Stories)
                        }

                    fun navigateTo(route: Route) {
                        backStack.add(route)
                    }

                    fun navigateUp() {
                        backStack.removeLastOrNull()
                    }

                    fun currentBottomNav(): Route.BottomNav? {
                        for (i in backStack.indices.reversed()) {
                            (backStack[i] as? Route.BottomNav)?.run {
                                return this
                            }
                        }
                        return null
                    }

                    fun <N : Route.BottomNav> currentStack(bottomNav: N): IntRange {
                        var last = backStack.indices.last
                        for (i in backStack.indices.reversed()) {
                            if (bottomNav::class.isInstance(backStack[i])) {
                                return i..last
                            }
                            if (backStack[i] is Route.BottomNav) {
                                last = i - 1
                            }
                        }
                        backStack.add(bottomNav)
                        val i = backStack.indices.last
                        return i..i
                    }
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val currentBottomNavDestination = currentBottomNav()
                                Route.BottomNav.entries.forEach { destination ->
                                    NavigationBarItem(
                                        selected = destination == currentBottomNavDestination,
                                        onClick = {
                                            val r = currentStack(destination.instance)
                                            val v = backStack.slice(r)
                                            backStack.removeRange(
                                                fromIndex = r.first,
                                                toIndex = r.last + 1,
                                            )
                                            backStack.addAll(v)
                                            if (backStack.first() != Route.BottomNav.Stories) {
                                                backStack.add(0, Route.BottomNav.Stories)
                                            }
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
                            backStack = backStack,
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
