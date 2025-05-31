package com.monoid.hackernews.common.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.TrendingUp
import androidx.compose.material.icons.twotone.Feedback
import androidx.compose.material.icons.twotone.Forum
import androidx.compose.material.icons.twotone.RssFeed
import androidx.compose.material.icons.twotone.Whatshot
import androidx.compose.material.icons.twotone.Work
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.ask
import com.monoid.hackernews.common.view.hot
import com.monoid.hackernews.common.view.itemlist.ItemsColumn
import com.monoid.hackernews.common.view.jobs
import com.monoid.hackernews.common.view.new
import com.monoid.hackernews.common.view.platform.PlatformLoadingIndicator
import com.monoid.hackernews.common.view.show
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import com.monoid.hackernews.common.view.trending
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.viewmodel.koinViewModel

enum class FabAction(
    val icon: ImageVector,
    val text: StringResource,
    val storyOrdering: StoryOrdering,
) {
    // TODO: post
    // Post(Icons.TwoTone.Add, Res.string.post),
    Jobs(Icons.TwoTone.Work, Res.string.jobs, StoryOrdering.Jobs) {
        @Composable
        override fun Compose(
            fabAction: FabAction,
            onClickLogin: () -> Unit,
            onClickItem: (Item) -> Unit,
            onClickReply: (ItemId) -> Unit,
            onClickUser: (Username) -> Unit,
            onClickUrl: (Url) -> Unit,
            contentPadding: PaddingValues,
            modifier: Modifier,
            content: @Composable (x: Pair<BoxScope, Boolean>) -> Unit,
        ) {
            val viewModel: StoriesViewModel = koinViewModel(
                // we set a key so a unique viewmodel is created for each story ordering
                key = fabAction.storyOrdering.toString(),
                extras = StoriesViewModel.extras(fabAction.storyOrdering),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            val lifecycleOwner = LocalLifecycleOwner.current
            LaunchedEffect(Unit) {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    for (event in viewModel.events) {
                        when (event) {
                            is StoriesViewModel.Event.Error -> {
                                // TODOa
                            }

                            is StoriesViewModel.Event.OpenLogin -> {
                                onClickLogin()
                            }
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
                    onClickFlag = {},
                    contentPadding = contentPadding,
                    modifier = modifier,
                ) {
                    content(Pair(this@Box, it))
                }
                if (uiState.loading && uiState.isRefreshing.not()) {
                    PlatformLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    },
    Ask(Icons.TwoTone.Forum, Res.string.ask, StoryOrdering.Ask) {
        @Composable
        override fun Compose(
            fabAction: FabAction,
            onClickLogin: () -> Unit,
            onClickItem: (Item) -> Unit,
            onClickReply: (ItemId) -> Unit,
            onClickUser: (Username) -> Unit,
            onClickUrl: (Url) -> Unit,
            contentPadding: PaddingValues,
            modifier: Modifier,
            content: @Composable (x: Pair<BoxScope, Boolean>) -> Unit,
        ) {
            val viewModel: StoriesViewModel = koinViewModel(
                // we set a key so a unique viewmodel is created for each story ordering
                key = fabAction.storyOrdering.toString(),
                extras = StoriesViewModel.extras(fabAction.storyOrdering),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
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
            Box {
                ItemsColumn(
                    itemsList = uiState.itemsList,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = viewModel::refreshItems, onVisibleItem = viewModel::updateItem,
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
                    content(Pair(this@Box, it))
                }
                if (uiState.loading && uiState.isRefreshing.not()) {
                    PlatformLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    },
    Show(Icons.TwoTone.Feedback, Res.string.show, StoryOrdering.Show) {
        @Composable
        override fun Compose(
            fabAction: FabAction,
            onClickLogin: () -> Unit,
            onClickItem: (Item) -> Unit,
            onClickReply: (ItemId) -> Unit,
            onClickUser: (Username) -> Unit,
            onClickUrl: (Url) -> Unit,
            contentPadding: PaddingValues,
            modifier: Modifier,
            content: @Composable (x: Pair<BoxScope, Boolean>) -> Unit,
        ) {
            val viewModel: StoriesViewModel = koinViewModel(
                // we set a key so a unique viewmodel is created for each story ordering
                key = fabAction.storyOrdering.toString(),
                extras = StoriesViewModel.extras(fabAction.storyOrdering),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
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
            Box {
                ItemsColumn(
                    itemsList = uiState.itemsList,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = viewModel::refreshItems, onVisibleItem = viewModel::updateItem,
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
                    content(Pair(this@Box, it))
                }
                if (uiState.loading && uiState.isRefreshing.not()) {
                    PlatformLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    },
    Hot(Icons.TwoTone.Whatshot, Res.string.hot, StoryOrdering.Hot) {
        @Composable
        override fun Compose(
            fabAction: FabAction,
            onClickLogin: () -> Unit,
            onClickItem: (Item) -> Unit,
            onClickReply: (ItemId) -> Unit,
            onClickUser: (Username) -> Unit,
            onClickUrl: (Url) -> Unit,
            contentPadding: PaddingValues,
            modifier: Modifier,
            content: @Composable (x: Pair<BoxScope, Boolean>) -> Unit,
        ) {
            val viewModel: StoriesViewModel = koinViewModel(
                // we set a key so a unique viewmodel is created for each story ordering
                key = fabAction.storyOrdering.toString(),
                extras = StoriesViewModel.extras(fabAction.storyOrdering),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
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
            Box {
                ItemsColumn(
                    itemsList = uiState.itemsList,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = viewModel::refreshItems, onVisibleItem = viewModel::updateItem,
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
                    content(Pair(this@Box, it))
                }
                if (uiState.loading && uiState.isRefreshing.not()) {
                    PlatformLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    },
    New(Icons.TwoTone.RssFeed, Res.string.new, StoryOrdering.New) {
        @Composable
        override fun Compose(
            fabAction: FabAction,
            onClickLogin: () -> Unit,
            onClickItem: (Item) -> Unit,
            onClickReply: (ItemId) -> Unit,
            onClickUser: (Username) -> Unit,
            onClickUrl: (Url) -> Unit,
            contentPadding: PaddingValues,
            modifier: Modifier,
            content: @Composable (x: Pair<BoxScope, Boolean>) -> Unit,
        ) {
            val viewModel: StoriesViewModel = koinViewModel(
                // we set a key so a unique viewmodel is created for each story ordering
                key = fabAction.storyOrdering.toString(),
                extras = StoriesViewModel.extras(fabAction.storyOrdering),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
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
            Box {
                ItemsColumn(
                    itemsList = uiState.itemsList,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = viewModel::refreshItems, onVisibleItem = viewModel::updateItem,
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
                    content(Pair(this@Box, it))
                }
                if (uiState.loading && uiState.isRefreshing.not()) {
                    PlatformLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    },
    Trending(Icons.AutoMirrored.TwoTone.TrendingUp, Res.string.trending, StoryOrdering.Trending) {
        @Composable
        override fun Compose(
            fabAction: FabAction,
            onClickLogin: () -> Unit,
            onClickItem: (Item) -> Unit,
            onClickReply: (ItemId) -> Unit,
            onClickUser: (Username) -> Unit,
            onClickUrl: (Url) -> Unit,
            contentPadding: PaddingValues,
            modifier: Modifier,
            content: @Composable (x: Pair<BoxScope, Boolean>) -> Unit,
        ) {
            val viewModel: StoriesViewModel = koinViewModel(
                // we set a key so a unique viewmodel is created for each story ordering
                key = fabAction.storyOrdering.toString(),
                extras = StoriesViewModel.extras(fabAction.storyOrdering),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
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
            Box {
                ItemsColumn(
                    itemsList = uiState.itemsList,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = viewModel::refreshItems, onVisibleItem = viewModel::updateItem,
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
                    content(Pair(this@Box, it))
                }
                if (uiState.loading && uiState.isRefreshing.not()) {
                    PlatformLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    },
    ;

    @Composable
    abstract fun Compose(
        fabAction: FabAction,
        onClickLogin: () -> Unit,
        onClickItem: (Item) -> Unit,
        onClickReply: (ItemId) -> Unit,
        onClickUser: (Username) -> Unit,
        onClickUrl: (Url) -> Unit,
        contentPadding: PaddingValues,
        modifier: Modifier = Modifier,
        content: @Composable (x: Pair<BoxScope, Boolean>) -> Unit,
    )
}
