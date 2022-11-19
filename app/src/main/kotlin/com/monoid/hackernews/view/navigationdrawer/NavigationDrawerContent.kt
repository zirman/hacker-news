package com.monoid.hackernews.view.navigationdrawer

import android.content.Context
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Login
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
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
import com.monoid.hackernews.shared.data.LoginAction
import com.monoid.hackernews.shared.data.Username
import com.monoid.hackernews.shared.data.settingsDataStore
import com.monoid.hackernews.shared.datastore.Authentication
import com.monoid.hackernews.shared.view.R
import com.monoid.hackernews.shared.navigation.MainNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawerContent(
    mainNavController: NavHostController,
    drawerState: DrawerState,
    onClickUser: (Username) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(state = rememberScrollState())
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
                    coroutineScope.launch { drawerState.close() }
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
                    coroutineScope.launch { drawerState.close() }

                    mainNavController.navigate(
                        route = MainNavigation.Login.routeWithArgs(LoginAction.Login)
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }

        Divider(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding),
        )

        navigationItemList.forEach { item ->
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
                    coroutineScope.launch { drawerState.close() }
                    mainNavController.navigate(route = item.route)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }

        Divider(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .padding(NavigationDrawerItemDefaults.ItemPadding),
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.TwoTone.Info,
                    contentDescription = stringResource(id = R.string.about_us),
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.about_us),
                    maxLines = 1,
                )
            },
            selected = false,
            onClick = {
                coroutineScope.launch { drawerState.close() }

                mainNavController.navigate(
                    route = MainNavigation.AboutUs.routeWithArgs(Unit)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.TwoTone.Settings,
                    contentDescription = stringResource(id = R.string.settings),
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.settings),
                    maxLines = 1,
                )
            },
            selected = false,
            onClick = {
                coroutineScope.launch { drawerState.close() }

                mainNavController.navigate(
                    route = MainNavigation.Settings.routeWithArgs(Unit)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
    }
}
