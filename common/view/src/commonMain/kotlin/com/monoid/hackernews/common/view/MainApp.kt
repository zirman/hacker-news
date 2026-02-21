package com.monoid.hackernews.common.view


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.home.TOP_LEVEL_ROUTES
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.logout.LogoutDialog
import com.monoid.hackernews.common.view.theme.AppTheme
import io.ktor.http.Url
import rememberNavigationState

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
                val showBottomBar = windowAdaptiveInfo.windowPosture.isTabletop ||
                    windowAdaptiveInfo.windowSizeClass.minWidthDp < windowAdaptiveInfo.windowSizeClass.minHeightDp
                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(showBottomBar) {
                            MainNavigationBar(navigationState, navigator)
                        }
                    }
                ) { paddingValues ->
                    Row {
                        AnimatedVisibility(showBottomBar.not()) {
                            MainNavigationRail(navigationState, navigator)
                        }
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
