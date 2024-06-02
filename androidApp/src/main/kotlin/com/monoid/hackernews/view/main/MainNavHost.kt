@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.view.main

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.monoid.hackernews.common.navigation.RouteMain
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.view.home.HomeScaffold

@Composable
fun MainNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RouteMain.Home,
        modifier = modifier,
    ) {
        composable<RouteMain.Home> {
            HomeScaffold(
                onClickBrowser = { item ->
                    item.url?.run {
                        navController.navigate(RouteMain.Browser(this))
                    }
                },
            )
        }

        composable<RouteMain.Browser> { backStackEntry ->
            val url = backStackEntry.toRoute<RouteMain.Browser>().url
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
                                        contentDescription = stringResource(id = R.string.back),
                                    )
                                },
                            )
                        },
                    )
                },
                content = { paddingValues ->
                    AndroidView(
                        factory = {
                            WebView(it).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        update = {
                            it.loadUrl(url)
                        },
                    )
                },
            )
        }
    }
}
