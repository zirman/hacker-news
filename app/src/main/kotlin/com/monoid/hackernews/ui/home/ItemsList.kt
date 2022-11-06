package com.monoid.hackernews.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.data.ItemListRow
import com.monoid.hackernews.shared.navigation.LoginAction
import com.monoid.hackernews.shared.navigation.Username
import com.monoid.hackernews.ui.itemlist.ItemList
import com.monoid.hackernews.shared.ui.util.notifyInput

@Composable
fun ItemsList(
    swipeRefreshState: SwipeRefreshState,
    listState: LazyListState,
    itemRows: State<List<ItemListRow>?>,
    showItemId: ItemId?,
    setSelectedItemId: (ItemId?) -> Unit,
    setDetailInteraction: (Boolean) -> Unit,
    onClickUser: (Username?) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String?) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit
) {
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { /* TODO */ },
        modifier = Modifier
            .fillMaxSize()
            .notifyInput { setDetailInteraction(false) },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
            )
        },
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.primary,
        ) {
            ItemList(
                itemRows = itemRows,
                selectedItem = showItemId,
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
    }
}
