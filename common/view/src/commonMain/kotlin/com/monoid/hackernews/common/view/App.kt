package com.monoid.hackernews.common.view

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.monoid.hackernews.common.view.main.LoginDialog
import com.monoid.hackernews.common.view.main.MainNavHost
import com.monoid.hackernews.common.view.theme.AppTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        AppTheme {
            Scrim {
                Box(contentAlignment = Alignment.Center) {
                    var showDialog by rememberSaveable {
                        mutableIntStateOf(0)
                    }
                    MainNavHost(
                        onClickLogin = {
                            showDialog = 1
                        },
                    )
                    if (showDialog != 0) {
                        LoginDialog(
                            onDismissRequest = {
                                showDialog = 0
                            }
                        )
                    }
                }
            }
        }
    }
}
