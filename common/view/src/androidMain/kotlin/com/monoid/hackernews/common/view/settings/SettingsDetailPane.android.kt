@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.no_setting_selected
import org.jetbrains.compose.resources.stringResource

@Suppress("ComposeUnstableReceiver", "UnusedReceiverParameter")
@Composable
fun ThreePaneScaffoldScope.SettingsDetailPane(
    settingsDetailUiState: SettingsDetailUiState?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (settingsDetailUiState) {
            SettingsDetailUiState.Profile -> {
                ProfileDetailPane()
            }

            SettingsDetailUiState.Appearance -> {
                AppearanceDetailPane()
            }

            null -> {
                Text(
                    text = stringResource(Res.string.no_setting_selected),
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            SettingsDetailUiState.Notifications -> {
                NotificationsPane()
            }

            SettingsDetailUiState.Help -> {
                HelpPane()
            }

            SettingsDetailUiState.TermsOfService -> {
                TermsOfServicePane()
            }

            SettingsDetailUiState.UserGuidelines -> {
                UserGuidelinesPane()
            }

            SettingsDetailUiState.SendFeedback -> {
                SendFeedbackPane()
            }

            SettingsDetailUiState.About -> {
                AboutPane()
            }
        }
    }
}
