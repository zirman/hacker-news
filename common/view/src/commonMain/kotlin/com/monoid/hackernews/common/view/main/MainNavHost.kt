package com.monoid.hackernews.common.view.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.ItemIdNavType
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.domain.navigation.UsernameNavType
import com.monoid.hackernews.common.view.comment.CommentDialog
import com.monoid.hackernews.common.view.home.HomeScaffold
import org.koin.compose.viewmodel.koinViewModel
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
            val viewModel: HomeViewModel = koinViewModel()
            HomeScaffold(
                onClickLogin = onClickLogin,
                onClickLogout = onClickLogout,
                onClickUser = { navController.navigate(Route.User(it)) },
                onClickReply = {
                    if (viewModel.isLoggedIn) {
                        navController.navigate(Route.Reply(it))
                    } else {
                        onClickLogin()
                    }
                },
                onClickUrl = onClickUrl,
            )
        }
        dialog<Route.User>(
            typeMap = mapOf(typeOf<Username>() to NavType.UsernameNavType),
        ) { navBackStackEntry ->
            Text(navBackStackEntry.toRoute<Route.User>().username.string)
        }
        dialog<Route.Reply>(
            typeMap = mapOf(typeOf<ItemId>() to NavType.ItemIdNavType),
        ) { navBackStackEntry ->
            CommentDialog(
                navBackStackEntry.toRoute<Route.Reply>().parentId,
                onDismiss = navController::navigateUp,
                modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()),
            )
        }
    }
}
