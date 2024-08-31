@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.view.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.monoid.hackernews.common.navigation.Route
import com.monoid.hackernews.view.home.HomeScaffold

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
                    item.url?.run {
                        navController.navigate(Route.Browser(this))
                    }
                },
                onClickLogin = onClickLogin,
            )
        }
    }
}
