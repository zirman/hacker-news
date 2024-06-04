@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProfileScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = ProfileViewModel.create(),
) {
    Box(modifier = modifier) {
//        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//        val (loading, x) = uiState
//        val itemsList = uiState.itemsList
//        if (itemsList != null) {
//            StoriesScaffold(
//                navigator = navigator,
//                listState = listState,
//                itemsList = itemsList,
//                onItemVisible = { item ->
//                    viewModel.updateItem(item.id)
//                },
//                onClickBrowser = onClickBrowser,
//            )
//        }
    }
}
