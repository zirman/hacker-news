package com.monoid.hackernews.common.view.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.itemlist.ItemsColumn
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import com.monoid.hackernews.common.view.stories.createStoriesViewModel

@Composable
actual fun HomeContent(
    currentDestination: BottomNav,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier,
) {
    val viewModel: StoriesViewModel = createStoriesViewModel(StoryOrdering.Trending)
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is StoriesViewModel.Event.Error -> {
                    // TODO
                }
                is StoriesViewModel.Event.NavigateLogin -> {
                    onClickLogin()
                }
            }
        }
    }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    ItemsColumn(
        listState = viewModel.listState,
        itemsList = uiState.itemsList,
        onVisibleItem = viewModel::updateItem,
        onClickItem = {},
        onClickReply = {},
        onClickUser = {},
        onClickUrl = onClickUrl,
        onClickUpvote = viewModel::toggleUpvoted,
        onClickFavorite = {},
        onClickFollow = {},
        onClickFlag = {},
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        modifier = Modifier.fillMaxHeight(),
    )
}
