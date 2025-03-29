@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onNavigateLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    Box(modifier = modifier) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val (loading, username) = uiState
        val scope = rememberCoroutineScope()
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                SettingsListPane(
                    username = username,
                    onClickLogin = onNavigateLogin,
                    onClickLogout = {
                        viewModel.logout()
                    },
                    onClickAppearance = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = SettingsDetailUiState.Appearance.ordinal,
                            )
                        }
                    },
                    onClickNotifications = {
                        // TODO
                    },
                    onClickHelp = {
                        // TODO
                        // https://news.ycombinator.com/newsfaq.html
                    },
                    onClickTermsOfService = {
                        // TODO
                        // https://www.ycombinator.com/legal/
                    },
                    onClickUserGuidelines = {
                        // TODO
                        // https://news.ycombinator.com/newsguidelines.html
                    },
                    onClickSendFeedback = {
                        // TODO
                        // hn@ycombinator.com
                        // monoids@gmail.com
                    },
                    onClickAbout = {
                        // TODO
                    },
                )
            },
            detailPane = {
                SettingsDetailPane(
                    settingsDetailUiState = (navigator.currentDestination?.contentKey as Int?)
                        ?.let { SettingsDetailUiState.entries[it] },
                )
            },
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
