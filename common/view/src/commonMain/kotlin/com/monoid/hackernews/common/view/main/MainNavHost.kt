package com.monoid.hackernews.common.view.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.domain.navigation.UsernameNavType
import com.monoid.hackernews.common.view.home.HomeScaffold
import kotlin.reflect.typeOf

@Composable
fun MainNavHost(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier,
    ) {
        composable<Route.Home> {
            HomeScaffold(
                onClickLogin = onClickLogin,
                onClickLogout = onClickLogout,
                onClickUser = { navController.navigate(Route.User(it)) },
                onClickUrl = onClickUrl,
            )
        }
        composable<Route.User>(typeMap = mapOf(typeOf<Username>() to NavType.UsernameNavType)) { navBackStackEntry ->
            Text(navBackStackEntry.toRoute<Route.User>().username.string)
        }
    }
}
