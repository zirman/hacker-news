package com.monoid.hackernews.common.view.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.home.HomeScaffold

@Composable
fun MainNavHost(
    onNavigateLogin: () -> Unit,
    onNavigateLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = rememberNavController(),
        startDestination = Route.Home,
        modifier = modifier,
    ) {
        composable<Route.Home> {
            HomeScaffold(
                onClickBrowser = { item ->
                    item.url
                        ?.let { Url(it) }
                        ?.run { openWebpage(this) }
                },
                onNavigateLogin = onNavigateLogin,
                onNavigateLogout = onNavigateLogout,
            )
        }
    }
}

expect fun openWebpage(url: Url): Boolean
