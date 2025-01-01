@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.view.itemdetail.WebViewPane

@Suppress("ComposeUnstableReceiver")
@Composable
fun ThreePaneScaffoldScope.StoriesExtraPane(url: String?, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(WindowInsets.safeDrawing.asPaddingValues())) {
        WebViewPane(
            url = url,
            modifier = Modifier.preferredWidth(320.dp),
        )
    }
}
