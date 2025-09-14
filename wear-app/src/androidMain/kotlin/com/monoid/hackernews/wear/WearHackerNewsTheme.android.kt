package com.monoid.hackernews.wear

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.view.settings.AppearanceViewModel
import com.monoid.hackernews.common.view.theme.LocalCommentIndentation

@Composable
fun WearHackerNewsTheme(
    viewModel: AppearanceViewModel = metroViewModel(),
    content: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MaterialTheme(
        colors = WearColorPalette,
        typography = WearTypography,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        content = {
            CompositionLocalProvider(
                LocalCommentIndentation provides uiState.paragraphIndent.em,
                content = content,
            )
        },
    )
}
