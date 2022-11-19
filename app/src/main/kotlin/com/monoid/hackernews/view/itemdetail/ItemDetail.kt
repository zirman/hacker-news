package com.monoid.hackernews.view.itemdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.data.ItemTreeRow
import com.monoid.hackernews.shared.data.LoginAction
import com.monoid.hackernews.shared.data.Username
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ItemDetail(
    itemId: ItemId,
    paddingValues: PaddingValues,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val mainViewModel: MainViewModel = viewModel()

    val itemListState: State<List<ItemTreeRow>?> =
        remember(itemId) { mainViewModel.itemTreeRepository.itemUiTreeFlow(itemId) }
            .collectAsState(initial = null)

    val refreshScope = rememberCoroutineScope()

    val (refreshing, setRefreshing) =
        remember { mutableStateOf(false) }

    fun refresh() = refreshScope
        .launch {
            setRefreshing(true)
            // TODO
            delay(400)
            setRefreshing(false)
        }

    val pullRefreshState: PullRefreshState =
        rememberPullRefreshState(
            refreshing = refreshing,
            onRefresh = ::refresh
        )

    Box(modifier.pullRefresh(pullRefreshState)) {
        CommentList(
            itemListState = itemListState,
            onClickUser = onClickUser,
            onClickReply = onClickReply,
            onClickBrowser = onClickBrowser,
            onNavigateLogin = onNavigateLogin,
            modifier = Modifier.fillMaxSize(),
            listState = listState,
            paddingValues = paddingValues,
        )

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
