package com.monoid.hackernews.view.itemlist

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.monoid.hackernews.common.data.Item

@Composable
fun ItemsColumn(
    itemsList: List<Item>?,
    onItemVisible: (Item) -> Unit,
    onItemClick: (Item) -> Unit,
    onOpenBrowser: (Item) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
    ) {
        items(itemsList.orEmpty(), { it.id.long }) { item ->
            LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
                onItemVisible(item)
            }
            Item(
                item = item,
                onClickDetail = {
                    onItemClick(item)
                },
                onClickReply = {
                },
                onClickUser = {
                },
                onClickBrowser = {
                    onOpenBrowser(item)
                },
                onClickUpvote = {
                },
                onClickFavorite = {
                },
                onClickFollow = {
                },
                onClickFlag = {
                },
                modifier = Modifier.animateItem(),
            )
        }
    }
}
