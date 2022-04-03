package com.monoid.hackernews.ui.navigationdrawer

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material.icons.twotone.Login
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.monoid.hackernews.MainNavigation
import com.monoid.hackernews.R
import com.monoid.hackernews.Username
import com.monoid.hackernews.datastore.Authentication
import com.monoid.hackernews.navigation.LoginAction
import com.monoid.hackernews.settingsDataStore
import com.monoid.hackernews.ui.main.MainState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawerContent(
    mainNavController: NavHostController,
    mainState: MainState,
    onClickUser: (Username) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedStories =
        remember {
            mainNavController.currentBackStackEntryFlow
                .map {
                    when (val mainNavigation = MainNavigation.fromRoute(it.destination.route)) {
                        is MainNavigation.Home ->
                            mainNavigation.argsFromRoute(it)
                        else ->
                            null
                    }
                }
        }
            .collectAsState(initial = null)
            .value

    Column(modifier = modifier) {
        val context: Context =
            LocalContext.current

        val authenticationState: State<Authentication?> =
            remember { context.settingsDataStore.data }
                .collectAsState(initial = null)

        val coroutineScope: CoroutineScope =
            rememberCoroutineScope()

        val authentication: Authentication? =
            authenticationState.value

        if (authentication?.password?.isNotEmpty() == true) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.TwoTone.Face,
                        contentDescription = authentication.username,
                    )
                },
                label = {
                    Text(
                        text = authentication.username,
                        maxLines = 1,
                    )
                },
                selected = false,
                onClick = {
                    onClickUser(Username(authentication.username))
                    coroutineScope.launch { mainState.drawerState.close() }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        } else {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.TwoTone.Login,
                        contentDescription = stringResource(id = R.string.login),
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.login),
                        maxLines = 1,
                    )
                },
                selected = false,
                onClick = {
                    coroutineScope.launch { mainState.drawerState.close() }

                    mainNavController.navigate(
                        route = MainNavigation.Login.routeWithArgs(LoginAction.Login)
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize(),
        ) {
            Divider(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .padding(NavigationDrawerItemDefaults.ItemPadding),
            )

            navigationItemList.forEach { item ->
                if (item != null) {
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = stringResource(id = item.titleId),
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = item.titleId),
                                maxLines = 1,
                            )
                        },
                        selected = item == storiesToNavigationItem[selectedStories],
                        onClick = {
                            coroutineScope.launch { mainState.drawerState.close() }
                            mainNavController.navigate(route = item.route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                } else {
                    Divider(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
            }
        }
    }
}
