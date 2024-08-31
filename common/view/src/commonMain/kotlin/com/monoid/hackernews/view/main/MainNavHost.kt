package com.monoid.hackernews.view.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.monoid.hackernews.common.navigation.Route
import com.monoid.hackernews.view.home.HomeScaffold
import com.monoid.hackernews.common.data.URL

@Composable
fun MainNavHost(onClickLogin: () -> Unit, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier,
    ) {
        composable<Route.Home> {
            HomeScaffold(
                onClickBrowser = { item ->
                    item.url
                        ?.let { URL(it) }
                        ?.run { openWebpage(this) }
                },
                onClickLogin = onClickLogin,
            )
        }
    }
}

expect fun openWebpage(url: URL): Boolean
