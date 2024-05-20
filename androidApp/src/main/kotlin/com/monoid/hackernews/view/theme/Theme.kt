package com.monoid.hackernews.view.theme

import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.ThemeViewModel
import com.monoid.hackernews.common.ui.util.rememberUseDarkTheme
import com.monoid.hackernews.common.view.R
import org.koin.androidx.compose.koinViewModel

private val LightThemeColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
)

private val DarkThemeColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
)

@Composable
fun AppTheme(
    viewModel: ThemeViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val useDarkTheme: Boolean = rememberUseDarkTheme()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MaterialTheme(
        colorScheme = if (uiState.dynamicIfAvailable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val context: Context =
                LocalContext.current

            if (useDarkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        } else if (useDarkTheme) {
            DarkThemeColors
        } else {
            LightThemeColors
        },
        typography = rememberAppTypography(uiState.font.toFontFamily()),
        content = content,
    )
}

fun HNFont.toFontFamily(): FontFamily = when (this) {
    HNFont.Cursive -> FontFamily.Cursive
    HNFont.Monospace -> FontFamily.Monospace
    HNFont.SansSerif -> FontFamily.SansSerif
    HNFont.Serif -> FontFamily.Serif
}

@StringRes
fun HNFont.toNameId(): Int = when (this) {
    HNFont.Cursive -> R.string.cursive
    HNFont.Monospace, -> R.string.monospace
    HNFont.SansSerif -> R.string.sans_serif
    HNFont.Serif -> R.string.serif
}
