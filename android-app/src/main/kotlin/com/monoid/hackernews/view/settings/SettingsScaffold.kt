@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onClickLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    Box(modifier = modifier) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val (loading, username) = uiState
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                SettingsListPane(
                    username = username,
                    onClickLogin = onClickLogin,
                    onClickLogout = {
                        viewModel.logout()
                    },
                    onClickStyle = {
                        navigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Detail,
                            content = SettingsDetailUiState.Styling,
                        )
                    },
                )
            },
            detailPane = {
                SettingsDetailPane(
                    settingsDetailUiState = navigator.currentDestination?.content as? SettingsDetailUiState?,
                )
            },
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
