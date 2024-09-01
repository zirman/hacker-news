@file:OptIn(ExperimentalSplitPaneApi::class)

package com.monoid.hackernews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.monoid.hackernews.common.data.dataStoreModule
import com.monoid.hackernews.common.data.databaseModule
import com.monoid.hackernews.common.data.networkModule
import com.monoid.hackernews.common.injection.dispatcherModule
import com.monoid.hackernews.common.injection.loggerModule
import com.monoid.hackernews.common.view.main.LoginDialog
import com.monoid.hackernews.common.view.main.MainNavHost
import com.monoid.hackernews.common.view.theme.AppTheme
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import java.awt.Cursor

fun main() {
    startKoin {
        modules(
            applicationModule,
            dispatcherModule,
            networkModule,
            databaseModule,
            dataStoreModule,
            loggerModule,
        )
    }

    application {
        Window(onCloseRequest = ::exitApplication) {
            AppTheme {
                KoinContext {
                    var showLoginDialog by rememberSaveable {
                        mutableStateOf(false)
                    }
                    Scaffold(
                        bottomBar = {
                            BottomAppBar(
                                actions = {
                                    IconButton(onClick = { /* do something */ }) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "Localized description"
                                        )
                                    }
                                    IconButton(onClick = { /* do something */ }) {
                                        Icon(
                                            Icons.Filled.Edit,
                                            contentDescription = "Localized description",
                                        )
                                    }
                                    IconButton(onClick = { /* do something */ }) {
                                        Icon(
                                            Icons.Filled.Mic,
                                            contentDescription = "Localized description",
                                        )
                                    }
                                    IconButton(onClick = { /* do something */ }) {
                                        Icon(
                                            Icons.Filled.Image,
                                            contentDescription = "Localized description",
                                        )
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
                                }
                            )
                        },
                    ) { innerPadding ->
                        HNPanes(
                            onOpenLogin = { showLoginDialog = true },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                    if (showLoginDialog) {
                        LoginDialog(onDismissRequest = { showLoginDialog = false })
                    }
                }
            }
        }
    }
}

@Composable
fun HNPanes(onOpenLogin: () -> Unit, modifier: Modifier = Modifier) {
    val splitterState = rememberSplitPaneState()
    HorizontalSplitPane(
        splitPaneState = splitterState,
        modifier = modifier,
    ) {
        first(320.dp) {
            MainNavHost(
                onClickLogin = onOpenLogin,
                modifier = Modifier.fillMaxSize(),
            )
        }
        second(320.dp) {
            MainNavHost(
                onClickLogin = onOpenLogin,
                modifier = Modifier.fillMaxSize(),
            )
        }
        splitter {
            visiblePart {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background),
                )
            }
            handle {
                Box(
                    Modifier
                        .markAsHandle()
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                        .background(SolidColor(Color.Gray), alpha = 0.50f)
                        .width(9.dp)
                        .fillMaxHeight(),
                )
            }
        }
    }
}
