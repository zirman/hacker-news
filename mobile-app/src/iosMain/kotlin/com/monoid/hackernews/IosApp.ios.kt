package com.monoid.hackernews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.monoid.hackernews.common.core.metro.LocalViewModelProviderFactory
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.Scrim
import com.monoid.hackernews.common.view.home.contentDescription
import com.monoid.hackernews.common.view.home.icon
import com.monoid.hackernews.common.view.home.label
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.logout.LogoutDialog
import com.monoid.hackernews.common.view.main.MainNavHost
import com.monoid.hackernews.common.view.theme.AppTheme
import dev.zacsweers.metro.createGraph
import io.ktor.http.Url
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass

@Composable
fun IosApp(onClickUrl: (Url) -> Unit) {
    val appGraph = remember { createGraph<IosAppGraph>() }
    CompositionLocalProvider(LocalViewModelProviderFactory provides object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            return appGraph.iosViewModelFactory.create(modelClass, extras)
        }
    }) {
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
                                                    // popUpTo(destination) {
                                                    //     saveState = true
                                                    // }
                                                    // launchSingleTop = true
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
                    ) { padding ->
                        val dir = LocalLayoutDirection.current
                        MainNavHost(
                            onClickLogin = {
                                showLoginDialog = true
                            },
                            onClickLogout = {
                                showLogoutDialog = true
                            },
                            onClickItem = { navController.nav(Route.Story(it.id)) },
                            onClickReply = { navController.nav(Route.Reply(it)) },
                            onClickUser = { navController.nav(Route.User(it)) },
                            onClickUrl = onClickUrl,
                            onClickAppearance = { navController.nav(Route.Settings.Appearance) },
                            onClickNotifications = { navController.nav(Route.Settings.Notifications) },
                            onClickHelp = { navController.nav(Route.Settings.Help) },
                            onClickTermsOfService = { navController.nav(Route.Settings.TermsOfService) },
                            onClickUserGuidelines = { navController.nav(Route.Settings.UserGuidelines) },
                            onClickSendFeedback = { navController.nav(Route.Settings.SendFeedback) },
                            onClickAbout = { navController.nav(Route.Settings.About) },
                            navController = navController,
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

fun <T : Any> NavHostController.nav(route: T) {
    navigate(
        route = route,
        navOptions = navOptions {
            popUpTo(route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        },
    )
}
