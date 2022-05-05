package com.monoid.hackernews.ui.itemdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.ui.main.MainState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun ItemDetail(
    mainState: MainState,
    itemId: ItemId,
    onClickUpvote: (ItemId) -> Unit,
    onClickUnUpvote: (ItemId) -> Unit,
    onClickFavorite: (ItemId) -> Unit,
    onClickUnFavorite: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope: CoroutineScope =
        rememberCoroutineScope()

    val itemListFactory = remember(itemId) { mainState.itemRepo.itemTreeFactory(itemId) }

    val itemList =
        remember(itemListFactory) {
            itemListFactory.itemUiListFlow().distinctUntilChanged()
        }.collectAsState(initial = null)

    LaunchedEffect(itemId) {
        itemListFactory.setItemExpanded(itemId, true)
    }

    val swipeRefreshState: SwipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = false)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            // TODO: reset cache
        },
        modifier = modifier,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
            )
        },
    ) {
        CommentList(
            mainState = mainState,
            itemList = itemList,
            updateItem = { item ->
                coroutineScope.launch { itemListFactory.itemUpdate(ItemId(item.id)) }
            },
            setExpanded = { itemId, expanded ->
                coroutineScope.launch { itemListFactory.setItemExpanded(itemId, expanded) }
            },
            onClickUpvote = onClickUpvote,
            onClickUnUpvote = onClickUnUpvote,
            onClickFavorite = onClickFavorite,
            onClickUnFavorite = onClickUnFavorite,
            onClickUser = onClickUser,
            onClickReply = onClickReply,
            onClickBrowser = onClickBrowser,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
