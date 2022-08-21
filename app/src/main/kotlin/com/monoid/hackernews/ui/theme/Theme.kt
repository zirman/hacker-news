package com.monoid.hackernews.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.MaterialTheme as MaterialTheme2

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
    useDarkTheme: Boolean = false,
    dynamicIfAvailable: Boolean = true,
    hnFont: HNFont = HNFont.Default,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (dynamicIfAvailable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
        typography = rememberAppTypography(hnFont.rememberFontFamily()),
    ) {
        // Shim in Material 3 styles into Material 2 components.
        MaterialTheme2(
            colors = MaterialTheme2.colors.copy(
                primary = MaterialTheme.colorScheme.primary,
                primaryVariant = MaterialTheme.colorScheme.primaryContainer,
                secondary = MaterialTheme.colorScheme.secondary,
                secondaryVariant = MaterialTheme.colorScheme.secondaryContainer,
                background = MaterialTheme.colorScheme.background,
                surface = MaterialTheme.colorScheme.surface,
                error = MaterialTheme.colorScheme.error,
                onPrimary = MaterialTheme.colorScheme.onPrimary,
                onSecondary = MaterialTheme.colorScheme.onSecondary,
                onBackground = MaterialTheme.colorScheme.onBackground,
                onSurface = MaterialTheme.colorScheme.onSurface,
                onError = MaterialTheme.colorScheme.onError,
                isLight = useDarkTheme.not(),
            ),
            typography = MaterialTheme2.typography.copy(
                h1 = MaterialTheme.typography.displayLarge,
                h2 = MaterialTheme.typography.displayMedium,
                h3 = MaterialTheme.typography.displaySmall,
                h4 = MaterialTheme.typography.headlineLarge,
                h5 = MaterialTheme.typography.headlineMedium,
                h6 = MaterialTheme.typography.headlineSmall,
                subtitle1 = MaterialTheme.typography.titleMedium,
                subtitle2 = MaterialTheme.typography.titleSmall,
                body1 = MaterialTheme.typography.bodyMedium,
                body2 = MaterialTheme.typography.bodySmall,
                button = MaterialTheme.typography.labelLarge,
                caption = MaterialTheme.typography.labelMedium,
                overline = MaterialTheme.typography.labelSmall,
            ),
            shapes = MaterialTheme2.shapes.copy(
                small = RoundedCornerShape(8.dp),
                medium = RoundedCornerShape(16.dp),
                large = RoundedCornerShape(32.dp)
            ),
            content = content,
        )
    }
}
