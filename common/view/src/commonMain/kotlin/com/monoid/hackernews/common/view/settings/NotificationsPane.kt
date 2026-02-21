package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.notifications
import org.jetbrains.compose.resources.stringResource

@Composable
fun NotificationsPane(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = metroViewModel(),
) {
    Surface(
        modifier = modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding),
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        ListItem(
            headlineContent = { Text(stringResource(Res.string.notifications)) },
            trailingContent = {
                Switch(
                    checked = uiState.notifications,
                    onCheckedChange = viewModel::onClickNotifications,
                )
            },
        )
    }
}
