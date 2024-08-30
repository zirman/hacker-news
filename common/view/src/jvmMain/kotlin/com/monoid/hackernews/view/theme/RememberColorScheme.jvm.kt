package com.monoid.hackernews.view.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.monoid.hackernews.common.data.Colors
import com.monoid.hackernews.common.data.LightDarkMode

@Composable
actual fun rememberColorScheme(
    lightDarkMode: LightDarkMode,
    colors: Colors
): ColorScheme {
    return LightThemeColors
}
