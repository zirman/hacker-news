package com.monoid.hackernews.common.view.itemlist

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
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username

@Composable
fun ItemsColumn(
    itemsList: List<Item>?,
    onVisibleItem: (Item) -> Unit,
    onClickItem: (Item) -> Unit,
    onClickReply: (Item) -> Unit,
    onClickUser: (Username) -> Unit,
    onOpenUrl: (Item) -> Unit,
    onClickUpvote: (Item) -> Unit,
    onClickFavorite: (Item) -> Unit,
    onClickFollow: (Item) -> Unit,
    onClickFlag: (Item) -> Unit,
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
                onVisibleItem(item)
            }
            Item(
                item = item,
                onClickItem = onClickItem,
                onClickReply = onClickReply,
                onClickUser = onClickUser,
                onOpenUrl = onOpenUrl,
                onClickUpvote = onClickUpvote,
                onClickFavorite = onClickFavorite,
                onClickFollow = onClickFollow,
                onClickFlag = onClickFlag,
                modifier = Modifier.animateItem(),
            )
        }
    }
}
