package com.monoid.hackernews.view.theme

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.Colors
import com.monoid.hackernews.common.data.LightDarkMode
import com.monoid.hackernews.common.data.Shape
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.cursive
import com.monoid.hackernews.common.view.cut
import com.monoid.hackernews.common.view.dark_mode
import com.monoid.hackernews.common.view.light_mode
import com.monoid.hackernews.common.view.monospace
import com.monoid.hackernews.common.view.rounded
import com.monoid.hackernews.common.view.sans_serif
import com.monoid.hackernews.common.view.serif
import com.monoid.hackernews.common.view.system
import com.monoid.hackernews.view.settings.PreferencesViewModel
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.viewmodel.koinViewModel

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

fun HNFont.toFontFamily(): FontFamily = when (this) {
    HNFont.Cursive -> FontFamily.Cursive
    HNFont.Monospace -> FontFamily.Monospace
    HNFont.SansSerif -> FontFamily.SansSerif
    HNFont.Serif -> FontFamily.Serif
}

fun HNFont.toNameId(): StringResource = when (this) {
    HNFont.Cursive -> Res.string.cursive
    HNFont.Monospace -> Res.string.monospace
    HNFont.SansSerif -> Res.string.sans_serif
    HNFont.Serif -> Res.string.serif
}

fun Shape.toNameId(): StringResource = when (this) {
    Shape.Rounded -> Res.string.rounded
    Shape.Cut -> Res.string.cut
}

fun LightDarkMode.toNameId(): StringResource = when (this) {
    LightDarkMode.System -> Res.string.system
    LightDarkMode.Light -> Res.string.light_mode
    LightDarkMode.Dark -> Res.string.dark_mode
}

@Composable
fun rememberShapes(shape: Shape, shapes: Shapes = MaterialTheme.shapes): Shapes = when (shape) {
    Shape.Rounded -> shapes
    Shape.Cut -> shapes.copy(
        extraSmall = CutCornerShape(
            topStart = shapes.extraSmall.topStart,
            topEnd = shapes.extraSmall.topEnd,
            bottomEnd = shapes.extraSmall.bottomEnd,
            bottomStart = shapes.extraSmall.bottomStart,
        ),
        small = CutCornerShape(
            topStart = shapes.small.topStart,
            topEnd = shapes.small.topEnd,
            bottomEnd = shapes.small.bottomEnd,
            bottomStart = shapes.small.bottomStart,
        ),
        medium = CutCornerShape(
            topStart = shapes.medium.topStart,
            topEnd = shapes.medium.topEnd,
            bottomEnd = shapes.medium.bottomEnd,
            bottomStart = shapes.medium.bottomStart,
        ),
        large = CutCornerShape(
            topStart = shapes.large.topStart,
            topEnd = shapes.large.topEnd,
            bottomEnd = shapes.large.bottomEnd,
            bottomStart = shapes.large.bottomStart,
        ),
        extraLarge = CutCornerShape(
            topStart = shapes.extraLarge.topStart,
            topEnd = shapes.extraLarge.topEnd,
            bottomEnd = shapes.extraLarge.bottomEnd,
            bottomStart = shapes.extraLarge.bottomStart,
        ),
    )
}

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

@SuppressLint("ComposeCompositionLocalUsage")
val LocalCommentIndentation = compositionLocalOf(structuralEqualityPolicy()) { 0 }
