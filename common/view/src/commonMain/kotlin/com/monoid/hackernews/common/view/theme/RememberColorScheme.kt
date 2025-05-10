package com.monoid.hackernews.common.view.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.model.Colors
import com.monoid.hackernews.common.data.model.LightDarkMode
import com.monoid.hackernews.common.view.settings.AppearanceViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun rememberColorScheme(
    lightDarkMode: LightDarkMode,
    colors: Colors, // TODO
): ColorScheme

@Composable
fun AppTheme(
    viewModel: AppearanceViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MaterialTheme(
        colorScheme = rememberColorScheme(uiState.lightDarkMode, uiState.colors),
        typography = rememberAppTypography(
            fontFamily = uiState.font.toFontFamily(),
            fontSizeDelta = uiState.fontSize,
            lineHeightDelta = uiState.lineHeight,
        ),
        shapes = rememberShapes(uiState.shape),
        content = {
            CompositionLocalProvider(
                LocalCommentIndentation provides uiState.paragraphIndent.em,
                content = content,
            )
        },
    )
}
