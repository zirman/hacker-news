@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.logout.LogoutDialog
import com.monoid.hackernews.common.view.theme.AppTheme
import io.ktor.http.Url

@Suppress("ComposeModifierMissing")
@Composable
fun MainApp(onClickUrl: (Url) -> Unit) {
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
                val windowAdaptiveInfo = currentWindowAdaptiveInfo()
                val useBottomBar = windowAdaptiveInfo.windowPosture.isTabletop ||
                    windowAdaptiveInfo.windowSizeClass.minWidthDp < windowAdaptiveInfo.windowSizeClass.minHeightDp
                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(useBottomBar) {
                            MainNavigationBar(navigationState, navigator)
                        }
                    }
                ) { paddingValues ->
                    val layoutDirection = LocalLayoutDirection.current
                    val start = paddingValues.calculateStartPadding(layoutDirection)
                    val end = paddingValues.calculateEndPadding(layoutDirection)
                    Row(
                        modifier = Modifier
                            .padding(start = start, end = end)
                            .consumeWindowInsets(ScaffoldDefaults.contentWindowInsets),
                    ) {

                        val contentPadding = rememberUpdatedState(
                            PaddingValues(
                                top = paddingValues.calculateTopPadding(),
                                bottom = paddingValues.calculateBottomPadding(),
                            ),
                        )
                        AnimatedVisibility(useBottomBar.not()) {
                            MainNavigationRail(
                                navigationState = navigationState,
                                navigator = navigator,
                                modifier = Modifier.padding(contentPadding.value),
                            )
                        }
                        MainNavDisplay(
                            entries = navigationState.toDecoratedEntries { key ->
                                key.navEntries(
                                    navigator = navigator,
                                    onClickUrl = onClickUrl,
                                    onShowLoginDialog = { showLoginDialog = true },
                                    contentPadding = contentPadding,
                                )
                            },
                            onBack = navigator::goBack,
                            windowAdaptiveInfo = windowAdaptiveInfo,
                        )
                    }
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
