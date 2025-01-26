package com.monoid.hackernews.common.view.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.monoid.hackernews.common.data.model.Colors
import com.monoid.hackernews.common.data.model.LightDarkMode

@Composable
actual fun rememberColorScheme(
    lightDarkMode: LightDarkMode,
    colors: Colors
): ColorScheme {
    return DarkThemeColors
}
