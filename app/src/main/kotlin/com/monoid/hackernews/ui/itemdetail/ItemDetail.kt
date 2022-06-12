package com.monoid.hackernews.ui.itemdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.data.ItemTreeRow
import com.monoid.hackernews.navigation.LoginAction

@Composable
fun ItemDetail(
    itemId: ItemId,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val mainViewModel: MainViewModel = viewModel()

    val itemListState: State<List<ItemTreeRow>?> =
        remember(itemId) { mainViewModel.itemRepo.itemUiTreeFlow(itemId) }
            .collectAsState(initial = null)

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
            itemListState = itemListState,
            onClickUser = onClickUser,
            onClickReply = onClickReply,
            onClickBrowser = onClickBrowser,
            onNavigateLogin = onNavigateLogin,
            modifier = Modifier.fillMaxSize(),
            listState = listState,
        )
    }
}
