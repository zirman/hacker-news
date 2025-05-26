package com.monoid.hackernews.common.view.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.model.LightDarkMode
import com.monoid.hackernews.common.view.settings.AppearanceViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun appColorScheme(): ColorScheme {
    val viewModel: AppearanceViewModel = koinViewModel()
    return when (viewModel.uiState.collectAsStateWithLifecycle().value.lightDarkMode) {
        LightDarkMode.System -> LightThemeColors // TODO: get system setting
        LightDarkMode.Light -> LightThemeColors
        LightDarkMode.Dark -> DarkThemeColors
    }
}

@Composable
actual fun AppTheme(
    viewModel: AppearanceViewModel,
    content: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MaterialTheme(
        colorScheme = appColorScheme(),
        typography = appTypography(
            fontFamily = uiState.font.toFontFamily(),
            fontSizeDelta = uiState.fontSize,
            lineHeightDelta = uiState.lineHeight,
        ),
        shapes = appShapes(uiState.shape),
        content = {
            CompositionLocalProvider(
                LocalCommentIndentation provides uiState.paragraphIndent.em,
                content = content,
            )
        },
    )
}
