@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.MainNavDisplay
import com.monoid.hackernews.common.view.currentBottomNav
import com.monoid.hackernews.common.view.currentStack
import com.monoid.hackernews.common.view.home.contentDescription
import com.monoid.hackernews.common.view.home.icon
import com.monoid.hackernews.common.view.home.selectedIcon
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.theme.AppTheme
import io.ktor.http.Url
import org.jetbrains.compose.resources.stringResource

@Composable
fun DesktopApp(onClickUrl: (Url) -> Unit) {
    AppTheme {
        var showLoginDialog by rememberSaveable {
            mutableStateOf(false)
        }
        if (showLoginDialog) {
            LoginDialog(onDismissRequest = { showLoginDialog = false })
        }
        val backStack: SnapshotStateList<Route> =
            rememberSerializable(serializer = SnapshotStateListSerializer()) {
                mutableStateListOf(Route.BottomNav.Stories)
            }
        Scaffold(
            bottomBar = {
                val currentBottomNavDestination: Route.BottomNav? =
                    backStack.currentBottomNav()
                BottomAppBar(
                    actions = {
                        Route.BottomNav.entries.forEach { destination ->
                            IconButton(
                                onClick = {
                                    val range = backStack.currentStack(Route.BottomNav.entries.first())
                                    val bottomNavStack = backStack.slice(range)
                                    backStack.removeRange(
                                        fromIndex = range.first,
                                        toIndex = range.last + 1,
                                    )
                                    backStack.addAll(bottomNavStack)
                                    if (backStack.first() != Route.BottomNav.Stories) {
                                        backStack.add(0, Route.BottomNav.Stories)
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = if (destination == currentBottomNavDestination) {
                                        destination.selectedIcon
                                    } else {
                                        destination.icon
                                    },
                                    contentDescription = stringResource(destination.contentDescription),
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* do something */ },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        ) {
                            Icon(Icons.Filled.Add, "Localized description")
                        }
                    },
                )
            },
        ) { innerPadding ->
            MainNavDisplay(
                backStack = backStack,
                onClickUrl = onClickUrl,
                onShowLoginDialog = { showLoginDialog = true },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
