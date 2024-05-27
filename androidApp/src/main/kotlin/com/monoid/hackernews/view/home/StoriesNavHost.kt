@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.SimpleItemUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoriesNavHost(
    listState: LazyListState,
    detailListState: LazyListState,
    onClickBrowser: (SimpleItemUiState) -> Unit,
    navigator: ThreePaneScaffoldNavigator<SimpleItemUiState>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val viewModel: StoriesViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val itemsList = uiState.itemsList
        if (itemsList != null) {
            StoryListDetailPaneScaffold(
                navigator = navigator,
                listState = listState,
                detailListState = detailListState,
                itemsList = itemsList,
                onItemVisible = { item ->
                    viewModel.updateItem(item.id)
                },
                onClickBrowser = onClickBrowser,
            )
        }
    }
}
