@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.stories

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.view.itemlist.ItemsColumn

@Suppress("ComposeUnstableReceiver")
@Composable
fun ThreePaneScaffoldScope.StoriesListPane(
    listState: LazyListState,
    itemsList: List<Item>?,
    onVisibleItem: (Item) -> Unit,
    onClickItem: (Item) -> Unit,
    onClickBrowser: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedPane(modifier = modifier) {
        ItemsColumn(
            listState = listState,
            itemsList = itemsList,
            onItemVisible = onVisibleItem,
            onItemClick = onClickItem,
            onOpenBrowser = onClickBrowser,
            modifier = Modifier
                .preferredWidth(320.dp)
                .fillMaxHeight(),
        )
    }
}
