@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.itemdetail.ItemDetailPane
import com.monoid.hackernews.common.view.no_story_selected
import org.jetbrains.compose.resources.stringResource

@Suppress("ComposeUnstableReceiver")
@Composable
fun ThreePaneScaffoldScope.StoriesDetailPane(
    itemId: ItemId?,
    onOpenBrowser: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedPane(modifier = modifier) {
        if (itemId == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(Res.string.no_story_selected),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        } else {
            key(itemId) {
                ItemDetailPane(
                    itemId = itemId,
                    onOpenBrowser = onOpenBrowser,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                )
            }
        }
    }
}