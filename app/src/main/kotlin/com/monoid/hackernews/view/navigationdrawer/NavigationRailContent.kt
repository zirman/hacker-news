package com.monoid.hackernews.view.navigationdrawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.monoid.hackernews.common.navigation.MainNavigation
import com.monoid.hackernews.common.view.R
import kotlinx.coroutines.flow.map

@Composable
fun NavigationRailContent(
    mainNavController: NavHostController,
) {
    val selectedStories =
        remember {
            mainNavController.currentBackStackEntryFlow
                .map {
                    when (val mainNavigation =
                        MainNavigation.fromRoute(it.destination.route)) {
                        is MainNavigation.Home ->
                            mainNavigation.argsFromRoute(it)
                        else ->
                            null
                    }
                }
        }
            .collectAsState(initial = null)
            .value

    navigationItemList.forEach { item ->
        NavigationRailItem(
            icon = {
                Icon(
                    imageVector = item.icon,
                    contentDescription = stringResource(id = item.titleId),
                )
            },
            label = {
                Text(
                    text = stringResource(id = item.titleId),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            },
            selected = item == storiesToNavigationItem[selectedStories],
            onClick = {
                mainNavController.navigate(route = item.route)
            },
        )
    }

    NavigationRailItem(
        icon = {
            Icon(
                imageVector = Icons.TwoTone.Info,
                contentDescription = stringResource(id = R.string.about_us),
            )
        },
        label = {
            Text(
                text = stringResource(id = R.string.about_us),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        },
        selected = false,
        onClick = {
            mainNavController.navigate(MainNavigation.AboutUs.routeWithArgs(Unit))
        },
    )

    NavigationRailItem(
        icon = {
            Icon(
                imageVector = Icons.TwoTone.Settings,
                contentDescription = stringResource(id = R.string.settings),
            )
        },
        label = {
            Text(
                text = stringResource(id = R.string.settings),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        },
        selected = false,
        onClick = {
            mainNavController.navigate(MainNavigation.Settings.routeWithArgs(Unit))
        },
    )
}
