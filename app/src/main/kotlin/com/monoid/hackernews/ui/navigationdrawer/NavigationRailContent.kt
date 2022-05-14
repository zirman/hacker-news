package com.monoid.hackernews.ui.navigationdrawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.monoid.hackernews.MainNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun NavigationRailContent(
    mainNavController: NavHostController,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val coroutineScope: CoroutineScope =
            rememberCoroutineScope()

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

        navigationItemList.forEach { item ->
            if (item != null) {
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
                        coroutineScope.launch { drawerState.close() }
                        mainNavController.navigate(route = item.route)
                    },
                )
            } else {
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
