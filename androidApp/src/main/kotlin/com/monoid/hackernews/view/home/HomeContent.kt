@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.home

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.SimpleItemUiState
import com.monoid.hackernews.common.navigation.BottomNav
import com.monoid.hackernews.view.profile.ProfileScaffold
import com.monoid.hackernews.view.stories.StoriesScaffold
import com.monoid.hackernews.view.stories.StoriesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeContent(currentDestination: BottomNav, onClickBrowser: (SimpleItemUiState) -> Unit) {
    val storiesNavigator = rememberListDetailPaneScaffoldNavigator<SimpleItemUiState>()
    val storiesListState = rememberLazyListState()
    val storiesDetailListState = rememberLazyListState()

    val favoritesNavigator =
        rememberListDetailPaneScaffoldNavigator<SimpleItemUiState>()
    val favoritesListState = rememberLazyListState()
    val favoritesDetailListState = rememberLazyListState()

    val profileNavigator = rememberListDetailPaneScaffoldNavigator<SimpleItemUiState>()
    val profileListState = rememberLazyListState()
    val profileDetailListState = rememberLazyListState()

    val viewModel: StoriesViewModel = koinViewModel()

    when (currentDestination) {
        BottomNav.Stories -> {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val itemsList = uiState.itemsList
            if (itemsList != null) {
                StoriesScaffold(
                    navigator = storiesNavigator,
                    listState = storiesListState,
                    detailListState = storiesDetailListState,
                    itemsList = itemsList,
                    onItemVisible = { item ->
                        viewModel.updateItem(item.id)
                    },
                    onClickBrowser = onClickBrowser,
                )
            }
        }

        BottomNav.Favorites -> {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val itemsList = uiState.itemsList
            if (itemsList != null) {
                StoriesScaffold(
                    navigator = favoritesNavigator,
                    listState = favoritesListState,
                    detailListState = favoritesDetailListState,
                    itemsList = itemsList,
                    onItemVisible = { item ->
                        viewModel.updateItem(item.id)
                    },
                    onClickBrowser = onClickBrowser,
                )
            }
        }

        BottomNav.Profile -> {
            ProfileScaffold(
                listState = profileListState,
                detailListState = profileDetailListState,
                onClickBrowser = onClickBrowser,
                navigator = profileNavigator,
            )
        }
    }
}
