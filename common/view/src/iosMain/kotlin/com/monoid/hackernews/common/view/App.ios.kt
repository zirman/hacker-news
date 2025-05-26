package com.monoid.hackernews.common.view

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.home.contentDescription
import com.monoid.hackernews.common.view.home.icon
import com.monoid.hackernews.common.view.home.label
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.logout.LogoutDialog
import com.monoid.hackernews.common.view.main.MainNavHost
import com.monoid.hackernews.common.view.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun App(onClickUrl: (Url) -> Unit) {
    AppTheme {
        Scrim {
            Box(contentAlignment = Alignment.Center) {
                var showLoginDialog by rememberSaveable { mutableStateOf(false) }
                var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
                val navController = rememberNavController()
                val startDestination = Route.BottomNav.Stories
                var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
                Scaffold(
                    bottomBar = {
                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                            Route.BottomNav.entries.forEachIndexed { index, destination ->
                                NavigationBarItem(
                                    selected = selectedDestination == index,
                                    onClick = {
                                        navController.navigate(
                                            route = destination,
                                            navOptions = navOptions {
                                                popUpTo(destination) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                            },
                                        )
                                        selectedDestination = index
                                    },
                                    icon = {
                                        Icon(
                                            destination.icon,
                                            contentDescription = stringResource(destination.contentDescription),
                                        )
                                    },
                                    label = { Text(stringResource(destination.label)) },
                                )
                            }
                        }
                    }
                ) {
                    MainNavHost(
                        onClickLogin = {
                            showLoginDialog = true
                        },
                        onClickLogout = {
                            showLogoutDialog = true
                        },
                        onClickReply = {
                            val route = Route.Reply(it)
                            navController.navigate(
                                route = route,
                                navOptions = navOptions {
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                },
                            )
                        },
                        onClickUser = {
                            val route = Route.User(it)
                            navController.navigate(
                                route = route,
                                navOptions = navOptions {
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                },
                            )
                        },
                        onClickUrl = onClickUrl,
                        navController = navController,
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
