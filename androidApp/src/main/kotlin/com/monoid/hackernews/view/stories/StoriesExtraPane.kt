@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.stories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.view.itemdetail.WebViewPane

@Suppress("ComposeUnstableReceiver")
@Composable
fun ThreePaneScaffoldScope.StoriesExtraPane(url: String?, modifier: Modifier = Modifier) {
    AnimatedPane(modifier = modifier) {
        Box(modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues())) {
            WebViewPane(
                url = url,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
