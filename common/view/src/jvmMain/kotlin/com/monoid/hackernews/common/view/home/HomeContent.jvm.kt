package com.monoid.hackernews.common.view.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.Route.BottomNav
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import io.ktor.http.Url

@Composable
fun HomeContent(
    currentDestination: BottomNav,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: StoriesViewModel = metroViewModel(
        extras = StoriesViewModel.extras(StoryOrdering.Trending),
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            for (event in viewModel.events) {
                when (event) {
                    is StoriesViewModel.Event.Error -> {
                        // TODO
                    }

                    is StoriesViewModel.Event.OpenLogin -> {
                        onClickLogin()
                    }
                }
            }
        }
    }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    ItemsColumn(
        itemsList = uiState.itemsList,
        onVisibleItem = viewModel::updateItem,
        onClickItem = {},
        onClickReply = {},
        onClickUser = {},
        onClickUrl = onClickUrl,
        onClickUpvote = viewModel::toggleUpvote,
        onClickFavorite = {},
        onClickFollow = {},
        onClickFlag = {},
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        modifier = Modifier.fillMaxHeight(),
    ) {}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsColumn(
    itemsList: List<Item>?,
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = contentPadding,
        ) {
            items(itemsList.orEmpty(), { it.id.long }) { item ->
                LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
                    onVisibleItem(item)
                }
                com.monoid.hackernews.common.view.itemlist.Item(
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
