@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.monoid.hackernews.common.view.itemlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.ItemType
import com.monoid.hackernews.common.data.model.Username
import io.ktor.http.Url

@Preview
@Composable
internal fun ItemsColumnPreview() {
    MaterialTheme {
        ItemsColumn(
            itemsList = listOf(
                Item(
                    id = ItemId(0),
                    lastUpdate = 123,
                    type = ItemType.Story,
                    time = 123,
                    deleted = false,
                    by = Username("Jane Doe"),
                    descendants = 0,
                    score = 5,
                    title = "Hello World",
                    text = null,
                    url = Url("https://www.wikipedia.org/"),
                    parent = null,
                    kids = emptyList(),
                    upvoted = false,
                    favorited = false,
                    flagged = false,
                    expanded = true,
                    followed = false,
                ),
                Item(
                    id = ItemId(1),
                    lastUpdate = 123,
                    type = ItemType.Story,
                    time = 123,
                    deleted = false,
                    by = Username("Jane Doe"),
                    descendants = 0,
                    score = 5,
                    title = "Hello World",
                    text = null,
                    url = Url("https://www.wikipedia.org/"),
                    parent = null,
                    kids = emptyList(),
                    upvoted = false,
                    favorited = false,
                    flagged = false,
                    expanded = true,
                    followed = false,
                ),
            ),
            isRefreshing = false,
            onRefresh = {},
            onVisibleItem = {},
            onClickItem = {},
            onClickReply = {},
            onClickUser = {},
            onClickUrl = {},
            onClickUpvote = {},
            onClickFavorite = {},
            onClickFollow = {},
            onClickFlag = {},
            contentPadding = PaddingValues(),
            modifier = Modifier.fillMaxHeight(),
        ) {}
    }
}

@Composable
fun ItemsColumn(
    itemsList: List<Item>?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onVisibleItem: (Item) -> Unit,
    onClickItem: (Item) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    onClickUpvote: (Item) -> Unit,
    onClickFavorite: (Item) -> Unit,
    onClickFollow: (Item) -> Unit,
    onClickFlag: (Item) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(
        cacheWindow = LazyLayoutCacheWindow(ahead = 500.dp),
    ),
    content: @Composable BoxScope.(scrolled: Boolean) -> Unit,
) {
    Box(modifier = modifier) {
        val state = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = state,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = state,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
        }
        content(listState.hasScrolled())
    }
}

private fun LazyListState.hasScrolled(): Boolean =
    firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
