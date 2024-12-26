package com.monoid.hackernews

import androidx.compose.runtime.Composable
import com.monoid.hackernews.common.view.main.MainNavHost
import com.monoid.hackernews.common.view.theme.AppTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        AppTheme {
            Scrim {
                MainNavHost(onClickLogin = {})
            }
        }
    }
}
