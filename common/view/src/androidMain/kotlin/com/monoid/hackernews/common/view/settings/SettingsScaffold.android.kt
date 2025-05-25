@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.layout.Box
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
import com.monoid.hackernews.common.view.platform.PlatformLoadingIndicator
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
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
                    onClickLogin = onClickLogin,
                    onClickLogout = onClickLogout,
                    onClickAppearance = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = SettingsDetailUiState.Appearance.ordinal,
                            )
                        }
                    },
                    onClickNotifications = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = SettingsDetailUiState.Notifications.ordinal,
                            )
                        }
                    },
                    onClickHelp = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = SettingsDetailUiState.Help.ordinal,
                            )
                        }
                    },
                    onClickTermsOfService = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = SettingsDetailUiState.TermsOfService.ordinal,
                            )
                        }
                    },
                    onClickUserGuidelines = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = SettingsDetailUiState.UserGuidelines.ordinal,
                            )
                        }
                    },
                    onClickSendFeedback = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = SettingsDetailUiState.SendFeedback.ordinal,
                            )
                        }
                    },
                    onClickAbout = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = SettingsDetailUiState.About.ordinal,
                            )
                        }
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
            PlatformLoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
