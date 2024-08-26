package com.monoid.hackernews.view.theme

import android.content.res.Configuration
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.Colors
import com.monoid.hackernews.common.data.LightDarkMode
import com.monoid.hackernews.view.settings.PreferencesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun rememberColorScheme(
    lightDarkMode: LightDarkMode,
    colors: Colors, // TODO
    configuration: Configuration = LocalConfiguration.current,
): ColorScheme = when (lightDarkMode) {
    LightDarkMode.System -> {
        if (configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        ) {
            DarkThemeColors
        } else {
            LightThemeColors
        }
    }

    LightDarkMode.Light -> LightThemeColors
    LightDarkMode.Dark -> DarkThemeColors
}

@Composable
fun AppTheme(
    viewModel: PreferencesViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CompositionLocalProvider(LocalCommentIndentation provides uiState.paragraphIndent.em) {
        MaterialTheme(
            colorScheme = rememberColorScheme(uiState.lightDarkMode, uiState.colors),
            typography = rememberAppTypography(
                fontFamily = uiState.font.toFontFamily(),
                fontSizeDelta = uiState.fontSize,
                lineHeightDelta = uiState.lineHeight,
                paragraphIndent = uiState.paragraphIndent,
            ),
            shapes = rememberShapes(uiState.shape),
            content = content,
        )
    }
}
