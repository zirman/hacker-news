@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
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
import org.koin.androidx.compose.koinViewModel

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
                // TODO: AnimatedPane(modifier = Modifier.fillMaxSize())
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
                    modifier = Modifier.preferredWidth(320.dp),
                )
            },
            detailPane = {
                // TODO: AnimatedPane(modifier = Modifier.fillMaxSize())
                Box(modifier = Modifier.fillMaxSize()) {
                    when (navigator.currentDestination?.content as? SettingsDetailUiState?) {
                        SettingsDetailUiState.Profile -> {
                            ProfileDetail()
                        }

                        SettingsDetailUiState.Styling -> {
                            PreferencesDetail()
                        }

                        null -> {
                            Text(
                                text = stringResource(id = R.string.no_setting_selected),
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }
            },
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
