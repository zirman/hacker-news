package com.monoid.hackernews.common.view.theme

import android.content.res.Configuration
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

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
