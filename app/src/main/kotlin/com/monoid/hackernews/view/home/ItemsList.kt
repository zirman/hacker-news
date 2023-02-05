package com.monoid.hackernews.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemListRow
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.view.itemlist.ItemList
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ItemsList(
    listState: LazyListState,
    pullRefreshState: PullRefreshState,
    itemRows: ImmutableList<ItemListRow>?,
    showItemId: ItemId?,
    refreshing: Boolean,
    paddingValues: PaddingValues,
    setSelectedItemId: (ItemId?) -> Unit,
    setDetailInteraction: (Boolean) -> Unit,
    onClickUser: (Username?) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String?) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.pullRefresh(pullRefreshState)) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.primary,
        ) {
            ItemList(
                itemRows = itemRows,
                selectedItem = showItemId,
                paddingValues = paddingValues,
                onClickDetail = {
                    setDetailInteraction(true)
                    setSelectedItemId(it)
                },
                onClickUser = onClickUser,
                onClickReply = onClickReply,
                onClickBrowser = onClickBrowser,
                onNavigateLogin = onNavigateLogin,
                listState = listState,
            )
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
