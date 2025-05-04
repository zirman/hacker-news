package com.monoid.hackernews.common.view.itemlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username

@Composable
fun ItemsColumn(
    itemsList: List<Item>?,
    onVisibleItem: (Item) -> Unit,
    onClickItem: (Item) -> Unit,
    onClickReply: (Item) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    onClickUpvote: (Item) -> Unit,
    onClickFavorite: (Item) -> Unit,
    onClickFollow: (Item) -> Unit,
    onClickFlag: (Item) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    content: @Composable BoxScope.(scrolled: Boolean) -> Unit,
) {
    Box {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = listState,
            contentPadding = contentPadding,
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
                    onClickUrl = onClickUrl,
                    onClickUpvote = onClickUpvote,
                    onClickFavorite = onClickFavorite,
                    onClickFollow = onClickFollow,
                    onClickFlag = onClickFlag,
                    modifier = Modifier.animateItem(),
                )
            }
        }
        content(listState.hasScrolled())
    }
}

private fun LazyListState.hasScrolled(): Boolean =
    firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
