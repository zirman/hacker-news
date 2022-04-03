package com.monoid.hackernews.ui.itemdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getItem
import com.monoid.hackernews.api.toRoomItem
import com.monoid.hackernews.room.Item
import com.monoid.hackernews.room.ItemRow
import com.monoid.hackernews.room.ItemTree
import com.monoid.hackernews.room.setExpanded
import com.monoid.hackernews.room.traverse
import com.monoid.hackernews.room.update
import com.monoid.hackernews.ui.main.MainState
import com.monoid.hackernews.ui.util.itemTreeSaver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ItemDetail(
    mainState: MainState,
    username: Username?,
    itemId: ItemId,
    onClickUpvote: (ItemId) -> Unit,
    onClickUnUpvote: (ItemId) -> Unit,
    onClickFavorite: (ItemId) -> Unit,
    onClickUnFavorite: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemTreeState: MutableState<ItemTree?> =
        rememberSaveable(stateSaver = itemTreeSaver) {
            mutableStateOf(null)
        }

    val itemTree: ItemTree? =
        itemTreeState.value

    fun setItemTree(itemTree: ItemTree) {
        itemTreeState.value = itemTree
    }

    LaunchedEffect(Unit) {
        var itemWithKids = mainState.itemDao.itemByIdWithKidsById(itemId.long)

        if (itemWithKids == null) {
            val itemFromApi = mainState.httpClient.getItem(itemId)

            if (itemFromApi.kids != null) {
                mainState.itemDao.insertIdsIgnore(itemFromApi.kids.map {
                    Item(
                        id = it.long,
                        parent = itemFromApi.id.long,
                    )
                })
            }

            mainState.itemDao.insertReplace(itemFromApi.toRoomItem())
            itemWithKids = mainState.itemDao.itemByIdWithKidsById(itemId.long)!!
        }

        val v = itemTreeState.value

        if (v == null) {
            setItemTree(
                ItemTree(
                    item = itemWithKids.item,
                    kids = itemWithKids.kids.map { ItemTree(it, null) },
                    expanded = true,
                )
            )
        } else {
            setItemTree(
                itemTreeState.value!!
                    .update(itemWithKids)
            )
        }
    }

    val itemList: List<ItemRow>? =
        remember(itemTree) { derivedStateOf { itemTree?.traverse() } }
            .value

    val coroutineScope: CoroutineScope =
        rememberCoroutineScope()

    val swipeRefreshState: SwipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = false)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            coroutineScope.launch {
                val itemFromApi = mainState.httpClient.getItem(itemId)

                if (itemFromApi.kids != null) {
                    mainState.itemDao.insertIdsIgnore(itemFromApi.kids.map {
                        Item(
                            id = it.long,
                            parent = itemFromApi.id.long,
                        )
                    })
                }

                mainState.itemDao.insertReplace(itemFromApi.toRoomItem())
                val itemWithKids = mainState.itemDao.itemByIdWithKidsById(itemId.long)!!

                setItemTree(
                    ItemTree(
                        item = itemWithKids.item,
                        kids = itemWithKids.kids.map { ItemTree(it, null) },
                        expanded = true,
                    )
                )
            }
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
            username = username,
            itemList = itemList ?: emptyList(),
            updateItemWithKids = { setItemTree(itemTreeState.value!!.update(it)) },
            setExpanded = { itemId, expanded ->
                setItemTree(
                    itemTreeState.value!!
                        .setExpanded(itemId, expanded)
                )
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
