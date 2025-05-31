@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        val scope = rememberCoroutineScope()
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                SettingsListPane(
                    username = uiState.username,
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
                    modifier = Modifier.preferredWidth(320.dp),
                )
            },
            detailPane = {
                SettingsDetailPane(
                    settingsDetailUiState = (navigator.currentDestination?.contentKey as Int?)
                        ?.let { SettingsDetailUiState.entries[it] },
                )
            },
        )
    }
}
