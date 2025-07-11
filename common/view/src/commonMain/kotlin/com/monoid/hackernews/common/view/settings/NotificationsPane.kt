package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.notifications
import com.monoid.hackernews.common.view.stories.detailContentInsetSides
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationsPane(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    Surface(modifier = modifier.fillMaxSize()) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        Column(
            modifier = modifier.padding(
                WindowInsets.safeDrawing
                    .only(detailContentInsetSides())
                    .asPaddingValues(),
            ),
        ) {
            ListItem(
                headlineContent = {
                    Text(stringResource(Res.string.notifications))
                },
                trailingContent = {
                    Switch(
                        checked = uiState.notifications,
                        onCheckedChange = viewModel::onClickNotifications,
                    )
                },
            )
        }
    }
}
