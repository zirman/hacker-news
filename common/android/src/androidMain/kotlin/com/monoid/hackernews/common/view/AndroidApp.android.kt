package com.monoid.hackernews.common.view

import androidx.compose.runtime.Composable
import io.ktor.http.Url

@Suppress("ComposeModifierMissing")
@Composable
fun AndroidApp(onClickUrl: (Url) -> Unit) {
    MainApp(onClickUrl)
}
