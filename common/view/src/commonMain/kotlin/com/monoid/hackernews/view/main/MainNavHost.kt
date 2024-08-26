@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.view.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.monoid.hackernews.common.navigation.Route
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.back
import com.monoid.hackernews.view.home.HomeScaffold
import com.monoid.hackernews.view.itemdetail.WebViewPane
import org.jetbrains.compose.resources.stringResource

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
            composable<Route.Browser> { backStackEntry ->
                val url = backStackEntry.toRoute<Route.Browser>().url
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = url)
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        navController.navigateUp()
                                    },
                                    content = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                                            contentDescription = stringResource(Res.string.back),
                                        )
                                    },
                                )
                            },
                        )
                    },
                    content = { paddingValues ->
                        WebViewPane(url = url, modifier = Modifier.padding(paddingValues))
                    },
                )
            }
        }
        if (showLoginDialog) {
            LoginDialog(onDismissRequest = { showLoginDialog = false })
        }
    }
}
