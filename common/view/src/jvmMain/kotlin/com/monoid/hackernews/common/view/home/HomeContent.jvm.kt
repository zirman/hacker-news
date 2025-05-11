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
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.itemlist.ItemsColumn
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun HomeContent(
    currentDestination: BottomNav,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier,
) {
    val viewModel: StoriesViewModel = koinViewModel(
        extras = StoriesViewModel.extras(StoryOrdering.Trending),
    )
    LaunchedEffect(Unit) {
        for (event in viewModel.events) {
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
