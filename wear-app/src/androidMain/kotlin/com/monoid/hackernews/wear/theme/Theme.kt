package com.monoid.hackernews.wear.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import com.monoid.hackernews.common.view.settings.PreferencesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HackerNewsTheme(
    viewModel: PreferencesViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MaterialTheme(
        colors = wearColorPalette,
        typography = Typography,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        content = {
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current.merge(
                    TextStyle(
                        textIndent = TextIndent(
                            firstLine = (LocalTextStyle.current.fontSize * uiState.paragraphIndent.em),
                        ),
                    ),
                ),
                content = content,
            )
        },
    )
}
