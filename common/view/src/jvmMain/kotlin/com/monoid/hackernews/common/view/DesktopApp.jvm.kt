@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.logout.LogoutDialog
import com.monoid.hackernews.common.view.theme.AppTheme
import io.ktor.http.Url
import org.jetbrains.compose.resources.stringResource

@Composable
fun DesktopApp(onClickUrl: (Url) -> Unit) {
    AppTheme {
        var showLoginDialog by rememberSaveable {
            mutableStateOf(false)
        }
        var showLogoutDialog by rememberSaveable {
            mutableStateOf(false)
        }
        if (showLoginDialog) {
            LoginDialog(onDismissRequest = { showLoginDialog = false })
        }
        if (showLogoutDialog) {
            LogoutDialog(onDismissRequest = { showLogoutDialog = false })
        }
        val navigationState = rememberNavigationState(
            startRoute = BottomNav.Stories,
            topLevelRoutes = TOP_LEVEL_ROUTES.keys,
        )
        val navigator: Navigator = remember { Navigator(navigationState) }
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    actions = {
                        TOP_LEVEL_ROUTES.forEach { (key, value) ->
                            IconButton(onClick = { navigator.navigate(key) }) {
                                Icon(
                                    imageVector = if (key == navigationState.topLevelRoute) {
                                        value.selectedIcon
                                    } else {
                                        value.icon
                                    },
                                    contentDescription = stringResource(value.description),
                                )
                            }
                        }
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
        ) { contentPadding ->
            val contentPadding = rememberUpdatedState(contentPadding)
            MainNavDisplay(
                entries = navigationState.toDecoratedEntries { key ->
                    key.navEntries(
                        navigator = navigator,
                        onClickUrl = onClickUrl,
                        onShowLoginDialog = { showLoginDialog = true },
                        onShowLogoutDialog = { showLogoutDialog = true },
                        contentPadding = contentPadding,
                    )
                },
                onBack = navigator::goBack,
            )
        }
    }
}
