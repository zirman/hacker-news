@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.view.itemdetail.ListItemDetailContentUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    Box(modifier = modifier) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                // TODO: AnimatedPane(modifier = Modifier.preferredWidth(320.dp)).
                Box(modifier = Modifier.preferredWidth(320.dp).fillMaxHeight()) {
//                    ProfileColumn(
//                        listState = viewModel.listState,
//                        itemsList = itemsList,
//                        onItemClick = { item ->
//                            navigator.navigateTo(
//                            )
//                        },
//                    )
                }
            },
            detailPane = {
                // TODO: AnimatedPane(modifier = Modifier.fillMaxSize())
                Box(modifier = Modifier.fillMaxSize()) {
                    val itemId =
                        (navigator.currentDestination?.content as? ListItemDetailContentUiState)?.itemId

                    if (itemId == null) {
                        Text(
                            text = stringResource(id = R.string.no_item_selected),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                    }
                }
            },
        )
    }
}
