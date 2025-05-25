@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.monoid.hackernews.common.view.theme

import android.content.res.Configuration
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.view.settings.AppearanceViewModel

@Composable
actual fun appColorScheme(): ColorScheme {
    val configuration: Configuration = LocalConfiguration.current
    val context = LocalContext.current
    val useDarkTheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        configuration.isNightModeActive
    } else {
        configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
    }
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        useDarkTheme -> {
            DarkThemeColors
        }
        else -> {
            LightThemeColors
        }
    }
}

@Composable
actual fun AppTheme(
    viewModel: AppearanceViewModel,
    content: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MaterialExpressiveTheme(
        colorScheme = appColorScheme(),
        motionScheme = MotionScheme.expressive(),
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
