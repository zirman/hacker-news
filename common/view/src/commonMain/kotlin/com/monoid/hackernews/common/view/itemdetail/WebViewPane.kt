package com.monoid.hackernews.common.view.itemdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun WebViewPane(url: String?, modifier: Modifier = Modifier)
