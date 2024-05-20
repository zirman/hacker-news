package com.monoid.hackernews.view.navigationdrawer

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Login
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.monoid.hackernews.common.data.Preferences
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.navigation.Route
import com.monoid.hackernews.common.view.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawerContent(
    preferences: DataStore<Preferences>,
    mainNavController: NavHostController,
    drawerState: DrawerState,
    onClickUser: (Username?) -> Unit,
    onClickAboutUs: () -> Unit,
    onClickSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
    ModalDrawerSheet(
        modifier = modifier
            .fillMaxHeight()
            .padding(
                start = safeDrawingPadding.calculateStartPadding(layoutDirection),
                end = safeDrawingPadding.calculateEndPadding(layoutDirection),
            )
            .verticalScroll(state = rememberScrollState()),
    ) {
        val selectedStory =
            remember {
                mainNavController.currentBackStackEntryFlow
                    .map { backStackEntry ->
                        when (backStackEntry.destination.route?.takeWhile { it != '/' }) {
                            Route.Home::class.simpleName -> backStackEntry.toRoute<Route.Home>().story
                            else -> null
                        }
                    }
            }
                .collectAsStateWithLifecycle(null)
                .value

        val coroutineScope: CoroutineScope =
            rememberCoroutineScope()

        val preferencesState: Preferences by remember { preferences.data }
            .collectAsStateWithLifecycle(Preferences())

        val auth = preferencesState
        if (auth.password.string.isNotEmpty()) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.TwoTone.Face,
                        contentDescription = auth.username.string,
                    )
                },
                label = {
                    Text(
                        text = auth.username.string,
                        maxLines = 1,
                    )
                },
                selected = false,
                onClick = {
                    onClickUser(Username(auth.username.string))
                    coroutineScope.launch { drawerState.close() }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        } else {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.TwoTone.Login,
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
                    onClickUser(auth.username)
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }

        HorizontalDivider(
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
                selected = item == storyToNavigationItem[selectedStory],
                onClick = {
                    coroutineScope.launch { drawerState.close() }
                    mainNavController.navigate(item.route)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }

        HorizontalDivider(
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
                onClickAboutUs()
                coroutineScope.launch { drawerState.close() }
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
                onClickSettings()
                coroutineScope.launch { drawerState.close() }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
    }
}
