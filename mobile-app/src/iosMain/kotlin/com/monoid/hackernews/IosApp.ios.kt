@file:OptIn(ExperimentalNativeApi::class)

package com.monoid.hackernews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import com.monoid.hackernews.common.core.metro.LocalViewModelProviderFactory
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.MainNavDisplay
import com.monoid.hackernews.common.view.Scrim
import com.monoid.hackernews.common.view.currentBottomNav
import com.monoid.hackernews.common.view.currentStack
import com.monoid.hackernews.common.view.home.contentDescription
import com.monoid.hackernews.common.view.home.icon
import com.monoid.hackernews.common.view.home.label
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.logout.LogoutDialog
import com.monoid.hackernews.common.view.stories.LocalPlatformContext
import com.monoid.hackernews.common.view.stories.PlatformContext
import com.monoid.hackernews.common.view.theme.AppTheme
import dev.zacsweers.metro.createGraph
import io.ktor.http.Url
import org.jetbrains.compose.resources.stringResource
import kotlin.experimental.ExperimentalNativeApi

@Composable
fun IosApp(onClickUrl: (Url) -> Unit) {
    val appGraph = retain { createGraph<IosAppGraph>() }
    CompositionLocalProvider(
        LocalViewModelProviderFactory provides appGraph.iosViewModelFactory,
        LocalPlatformContext provides PlatformContext(Unit),
    ) {
        AppTheme {
            Scrim {
                Box(contentAlignment = Alignment.Center) {
                    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
                    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
                    val backStack: SnapshotStateList<Route> =
                        rememberSerializable(serializer = SnapshotStateListSerializer()) {
                            mutableStateListOf(Route.BottomNav.Stories)
                        }
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val currentBottomNavDestination: Route.BottomNav? =
                                    backStack.currentBottomNav()
                                Route.BottomNav.entries.forEach { destination ->
                                    NavigationBarItem(
                                        selected = destination == currentBottomNavDestination,
                                        onClick = {
                                            val r = backStack.currentStack(Route.BottomNav.entries.first())
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
                    ) { paddingValues ->
                        MainNavDisplay(
                            backStack = backStack,
                            onClickUrl = onClickUrl,
                            onShowLoginDialog = { showLoginDialog = true },
                            modifier = Modifier
                                .consumeWindowInsets(ScaffoldDefaults.contentWindowInsets)
                                .padding(PaddingValues(bottom = paddingValues.calculateBottomPadding())),
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
