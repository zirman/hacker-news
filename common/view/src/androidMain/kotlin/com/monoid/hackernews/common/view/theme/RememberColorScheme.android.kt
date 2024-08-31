package com.monoid.hackernews.common.view.theme

import android.content.res.Configuration
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.monoid.hackernews.common.data.model.Colors
import com.monoid.hackernews.common.data.model.LightDarkMode

@Composable
actual fun rememberColorScheme(
    lightDarkMode: LightDarkMode,
    colors: Colors, // TODO
): ColorScheme {
    val configuration: Configuration = LocalConfiguration.current
    return when (lightDarkMode) {
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
}
