package com.monoid.hackernews

import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.monoid.hackernews.common.dataStoreModule
import com.monoid.hackernews.common.databaseModule
import com.monoid.hackernews.common.injection.dispatcherModule
import com.monoid.hackernews.common.injection.loggerModule
import com.monoid.hackernews.common.networkModule
import com.monoid.hackernews.view.main.LoginDialog
import com.monoid.hackernews.view.main.MainNavHost
import com.monoid.hackernews.view.theme.AppTheme
import org.koin.core.context.startKoin

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
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                                ) {
                                    Icon(Icons.Filled.Add, "Localized description")
                                }
                            }
                        )
                    },
                ) { innerPadding ->
                    MainNavHost(
                        onClickLogin = { showLoginDialog = true },
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
