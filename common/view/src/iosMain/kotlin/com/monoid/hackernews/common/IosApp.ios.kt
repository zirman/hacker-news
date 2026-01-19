@file:OptIn(ExperimentalNativeApi::class)

package com.monoid.hackernews.common

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.core.metro.LocalViewModelProviderFactory
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.MainNavDisplay
import com.monoid.hackernews.common.view.Navigator
import com.monoid.hackernews.common.view.Scrim
import com.monoid.hackernews.common.view.home.TOP_LEVEL_ROUTES
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.logout.LogoutDialog
import com.monoid.hackernews.common.view.navEntries
import com.monoid.hackernews.common.view.stories.LocalPlatformContext
import com.monoid.hackernews.common.view.stories.PlatformContext
import com.monoid.hackernews.common.view.theme.AppTheme
import dev.zacsweers.metro.createGraph
import io.ktor.http.Url
import org.jetbrains.compose.resources.stringResource
import rememberNavigationState
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
                    val navigationState = rememberNavigationState(
                        startRoute = BottomNav.Stories,
                        topLevelRoutes = TOP_LEVEL_ROUTES.keys,
                    )
                    val navigator = remember { Navigator(navigationState) }
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                TOP_LEVEL_ROUTES.forEach { (key, value) ->
                                    NavigationBarItem(
                                        selected = key == navigationState.topLevelRoute,
                                        onClick = { navigator.navigate(key) },
                                        icon = {
                                            Icon(
                                                imageVector = if (key == navigationState.topLevelRoute) value.selectedIcon else value.icon,
                                                contentDescription = stringResource(value.description),
                                            )
                                        },
                                        label = { Text(stringResource(value.label)) },
                                    )
                                }
                            }
                        }
                    ) { paddingValues ->
                        MainNavDisplay(
                            entries = navigationState.toDecoratedEntries { key ->
                                key.navEntries(
                                    navigator = navigator,
                                    onClickUrl = onClickUrl,
                                    onShowLoginDialog = { showLoginDialog = true },
                                )
                            },
                            onBack = navigator::goBack,
                            modifier = Modifier
                                .consumeWindowInsets(ScaffoldDefaults.contentWindowInsets)
                                .padding(PaddingValues(bottom = paddingValues.calculateBottomPadding())),
                        )
                    }
                    if (showLoginDialog) {
                        LoginDialog(onDismissRequest = { showLoginDialog = false })
                    }
                    if (showLogoutDialog) {
                        LogoutDialog(onDismissRequest = { showLogoutDialog = false })
                    }
                }
            }
        }
    }
}
