package com.monoid.hackernews.common.view

import androidx.compose.runtime.Composable
import io.ktor.http.Url

@Composable
expect fun App(onClickUrl: (Url) -> Unit)
