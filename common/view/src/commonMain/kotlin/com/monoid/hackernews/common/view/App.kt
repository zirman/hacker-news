package com.monoid.hackernews.common.view

import androidx.compose.runtime.Composable
import com.monoid.hackernews.common.data.Url

@Composable
expect fun App(onClickUrl: (Url) -> Unit)
