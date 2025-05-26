package com.monoid.hackernews.common.view.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.itemlist.ItemsColumn
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScaffold(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUser: (Username) -> Unit,
    onClickStory: (Item) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: StoriesViewModel = koinViewModel(
        extras = StoriesViewModel.extras(StoryOrdering.Trending),
    )
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    ItemsColumn(
        itemsList = uiState.itemsList,
        isRefreshing = false,
        onRefresh = {},
        onVisibleItem = viewModel::updateItem,
        onClickItem = onClickStory,
        onClickReply = {},
        onClickUser = {},
        onClickUrl = onClickUrl,
        onClickUpvote = {},
        onClickFavorite = {},
        onClickFollow = {},
        onClickFlag = {},
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        modifier = modifier.fillMaxHeight(),
    ) {}
}
