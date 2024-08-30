@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.view.main

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.monoid.hackernews.common.navigation.Route
import com.monoid.hackernews.view.home.HomeScaffold

@Composable
fun MainNavHost(modifier: Modifier = Modifier) {
    var showLoginDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val navController = rememberNavController()
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Route.Home,
        ) {
            composable<Route.Home> {
                HomeScaffold(
                    onClickBrowser = { item ->
                        item.url?.run {
                            navController.navigate(Route.Browser(this))
                        }
                    },
                    onClickLogin = {
                        showLoginDialog = true
                    },
                )
            }
        }
        if (showLoginDialog) {
            LoginDialog(onDismissRequest = { showLoginDialog = false })
        }
    }
}
