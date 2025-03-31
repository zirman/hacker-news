@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.itemlist.ItemsColumn

@Suppress("ComposeUnstableReceiver")
@Composable
fun ThreePaneScaffoldScope.StoriesListPane(
    listState: LazyListState,
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
    modifier: Modifier = Modifier,
) {
    ItemsColumn(
        listState = listState,
        itemsList = itemsList,
        onVisibleItem = onVisibleItem,
        onClickItem = onClickItem,
        onClickReply = onClickReply,
        onClickUser = onClickUser,
        onClickUrl = onClickUrl,
        onClickUpvote = onClickUpvote,
        onClickFavorite = onClickFavorite,
        onClickFollow = onClickFollow,
        onClickFlag = onClickFlag,
        modifier = modifier
            .preferredWidth(320.dp)
            .fillMaxHeight(),
    )
}
