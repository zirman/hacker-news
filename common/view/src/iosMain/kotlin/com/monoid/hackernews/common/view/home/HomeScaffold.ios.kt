package com.monoid.hackernews.common.view.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.itemlist.ItemsColumn
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StoriesPane(
    onClickLogin: () -> Unit,
    onClickUser: (Username) -> Unit,
    onClickStory: (Item) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: StoriesViewModel = koinViewModel(
        extras = StoriesViewModel.extras(StoryOrdering.Trending),
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            for (event in viewModel.events) {
                when (event){
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
        isRefreshing = false,
        onRefresh = viewModel::refreshItems,
        onVisibleItem = viewModel::updateItem,
        onClickItem = onClickStory,
        onClickReply = onClickReply,
        onClickUser = onClickUser,
        onClickUrl = onClickUrl,
        onClickUpvote = viewModel::toggleUpvote,
        onClickFavorite = viewModel::toggleFavorite,
        onClickFollow = viewModel::toggleFollow,
        onClickFlag = viewModel::toggleFlagged,
        contentPadding = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Top)
            .asPaddingValues(),
        modifier = modifier.fillMaxHeight(),
    ) {}
}
