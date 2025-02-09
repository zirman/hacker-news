package com.monoid.hackernews.common.view.home

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.itemlist.ItemsColumn
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.createStoriesViewModel

@Composable
actual fun HomeContent(
    currentDestination: BottomNav,
    onClickBrowser: (Item) -> Unit,
    onNavigateLogin: () -> Unit,
    modifier: Modifier,
) {
    val viewModel: StoriesViewModel = createStoriesViewModel(key = "default")
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    ItemsColumn(
        listState = viewModel.listState,
        itemsList = uiState.itemsList,
        onVisibleItem = viewModel::updateItem,
        onClickItem = {},
        onClickReply = {},
        onClickUser = {},
        onOpenUrl = onClickBrowser,
        onClickUpvote = {},
        onClickFavorite = {},
        onClickFollow = {},
        onClickFlag = {},
        modifier = modifier.fillMaxHeight(),
    )
}
