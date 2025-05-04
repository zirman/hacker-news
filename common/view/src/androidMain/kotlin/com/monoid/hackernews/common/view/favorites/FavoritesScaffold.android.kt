@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)
package com.monoid.hackernews.common.view.favorites

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.view.stories.StoriesDetailPane
import com.monoid.hackernews.common.view.stories.StoriesListPane
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import com.monoid.hackernews.common.view.stories.createStoriesViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoritesScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onClickLogin: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StoriesViewModel = createStoriesViewModel(StoryOrdering.Trending),
) {
    Box(modifier = modifier) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is StoriesViewModel.Event.Error -> {
                        Toast
                            .makeText(
                                context,
                                "An error occurred: ${event.message}",
                                Toast.LENGTH_SHORT,
                            )
                            .show()
                    }

                    is StoriesViewModel.Event.NavigateLogin -> {
                        onClickLogin()
                    }
                }
            }
        }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val (loading, itemsList) = uiState
        val scope = rememberCoroutineScope()
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                StoriesListPane(
                    listState = viewModel.listState,
                    itemsList = itemsList,
                    onVisibleItem = viewModel::updateItem,
                    onClickItem = { item ->
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = "${item.id.long}",
                            )
                        }
                    },
                    onClickReply = {},
                    onClickUser = {},
                    onClickUrl = onClickUrl,
                    onClickUpvote = viewModel::toggleUpvoted,
                    onClickFavorite = {},
                    onClickFollow = {},
                    onClickFlag = {},
                    contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
                )
            },
            detailPane = {
                val itemId = (navigator.currentDestination?.contentKey as? String)
                    ?.toLong()
                    ?.let { ItemId(it) }
                StoriesDetailPane(
                    itemId = itemId,
                    onClickUrl = onClickUrl,
                )
            },
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
