package com.monoid.hackernews.common.view.favorites

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.itemlist.ItemsColumn
import org.koin.compose.viewmodel.koinViewModel

@Suppress("ComposeUnstableReceiver")
@Composable
fun FavoriteStoriesListPane(
    username: Username,
    onClickItem: (Item) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    onClickLogin: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val viewModel: FavoritesViewModel = koinViewModel(
        // we set a key so a unique viewmodel is created for each story ordering
        key = username.toString(),
        extras = FavoritesViewModel.extras(username),
    )
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        for (event in viewModel.events) {
            when (event) {
                is FavoritesViewModel.Event.Error -> {
                    Toast
                        .makeText(
                            context,
                            "An error occurred: ${event.message}",
                            Toast.LENGTH_SHORT,
                        )
                        .show()
                }

                is FavoritesViewModel.Event.NavigateLogin -> {
                    onClickLogin()
                }
            }
        }
    }
    Box {
        ItemsColumn(
            itemsList = uiState.itemsList,
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refreshItems,
            onVisibleItem = viewModel::updateItem,
            onClickItem = onClickItem,
            onClickReply = onClickReply,
            onClickUser = onClickUser,
            onClickUrl = onClickUrl,
            onClickUpvote = viewModel::toggleUpvote,
            onClickFavorite = viewModel::toggleFavorite,
            onClickFollow = viewModel::toggleFollow,
            onClickFlag = viewModel::toggleFlagged,
            contentPadding = contentPadding,
            modifier = modifier,
        ) {
            //content(Pair(this@Box, it))
        }
        if (uiState.loading && uiState.isRefreshing.not()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
