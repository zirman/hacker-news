package com.monoid.hackernews.common.view

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainNavigationBar(
    navigationState: NavigationState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        TOP_LEVEL_ROUTES.forEach { (key, value) ->
            NavigationBarItem(
                selected = key == navigationState.topLevelRoute,
                onClick = { navigator.navigate(key) },
                icon = {
                    Icon(
                        imageVector = if (key == navigationState.topLevelRoute) {
                            value.selectedIcon
                        } else {
                            value.icon
                        },
                        contentDescription = stringResource(value.description),
                    )
                },
                label = { Text(stringResource(value.label)) },
            )
        }
    }
}

@Composable
fun MainNavigationRail(
    navigationState: NavigationState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    NavigationRail(modifier = modifier) {
        TOP_LEVEL_ROUTES.forEach { (key, value) ->
            NavigationRailItem(
                selected = key == navigationState.topLevelRoute,
                onClick = { navigator.navigate(key) },
                icon = {
                    Icon(
                        imageVector = if (key == navigationState.topLevelRoute) {
                            value.selectedIcon
                        } else {
                            value.icon
                        },
                        contentDescription = stringResource(value.description),
                    )
                },
                label = { Text(stringResource(value.label)) },
            )
        }
    }
}
